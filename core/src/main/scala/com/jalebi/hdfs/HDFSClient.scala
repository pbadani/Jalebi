package com.jalebi.hdfs

import java.io.ByteArrayOutputStream
import java.net.URI

import com.jalebi.api.{Jalebi, Triplets}
import com.jalebi.common.Logging
import com.jalebi.exception.{DatasetCorruptException, DatasetNotFoundException, DuplicateDatasetException}
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.yarn.conf.YarnConfiguration

case class HDFSClient(fs: FileSystem) extends Logging {

  import com.jalebi.common.JalebiUtils._

  @throws[DuplicateDatasetException]
  def createDataset(name: String, triplets: => Iterator[Triplets]): Unit = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    if (datasetExists(name)) {
      throw new DuplicateDatasetException(s"Dataset '$name' is already present at $filePath.")
    }
    LOGGER.info(s"Creating Dataset '$name' at $filePath.")
    triplets.zipWithIndex.foreach {
      case (t, index) =>
        val partFileName = s"part-$index"
        val fileName = Seq(filePath, partFileName).mkString("/")
        val os = fs.create(fileName)
        val outputStream = AvroOutputStream.data[Triplets](os)
        LOGGER.info(s"Writing $partFileName for Dataset '$name' at $filePath.")
        outputStream.write(t)
        outputStream.flush()
        outputStream.close()
    }
  }

  @throws[DatasetCorruptException]
  def loadDataset(name: String, parts: Set[String]): Jalebi = {
    val existingParts = listDatasetParts(name)
    val missingParts = parts.diff(existingParts)
    if (missingParts.nonEmpty) {
      throw new DatasetCorruptException(s"Missing parts ${missingParts.mkString(", ")}")
    }
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    Jalebi(name, parts.flatMap(partFileName => {
      val fileName = Seq(filePath, partFileName).mkString("/")
      val is = fs.open(fileName)
      val os = new ByteArrayOutputStream()
      IOUtils.copyBytes(is, os, 4096, false)
      LOGGER.info(s"Reading $partFileName for Dataset '$name' at $filePath.")
      val inputStream = AvroInputStream.data[Triplets](os.toByteArray)
      val triplets = inputStream.iterator.toSet
      inputStream.close()
      is.close()
      os.close()
      triplets
    }))
  }

  @throws[DatasetNotFoundException]
  def ensureDatasetExists(name: String): Unit = {
    datasetExists(name, Some(throw new DatasetNotFoundException(s"Dataset '$name' not found.")))
  }

  def datasetExists(name: String, actionIfNotExists: => Option[() => Unit] = None): Boolean = {
    val fileExists = fs.exists(s"${HDFSClientConstants.datasetParentDirectory}$name")
    LOGGER.debug(s"Dataset '$name' ${if (fileExists) "exists." else "doesn't exist."}")
    if (!fileExists && actionIfNotExists.isDefined) {
      actionIfNotExists.get()
    }
    fileExists
  }

  def listDatasets(): Set[String] = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}/"
    val files = listDirectory(filePath)
    LOGGER.info(s"Listing Datasets at $filePath : ${
      if (files.isEmpty) s"No Dataset found." else s"Datasets found: [${files.mkString(", ")}]."
    }")
    files
  }

  @throws[DatasetNotFoundException]
  def listDatasetParts(name: String): Set[String] = {
    ensureDatasetExists(name)
    val parts = listDirectory(s"${HDFSClientConstants.datasetParentDirectory}$name")
    LOGGER.info(s"Listing parts for Dataset '$name' - [${parts.mkString(", ")}].")
    parts
  }

  def deleteDataset(name: String): Unit = {
    if (datasetExists(name)) {
      LOGGER.info(s"Deleting dataset '$name'.")
      fs.delete(s"${HDFSClientConstants.datasetParentDirectory}$name", true)
    }
  }

  private def listDirectory(path: Path): Set[String] = {
    import com.jalebi.common.JalebiUtils._
    fs.listFiles(path, false).map(_.getPath.getName).toSet
  }

  def deleteDirectory(): Unit = {
    fs.delete(HDFSClientConstants.datasetParentDirectory, true)
  }

}

case class HostPort(scheme: String, host: String, port: Long) extends Serializable {

  def getURI: URI = new URI(s"$scheme://$host:$port")

}

object HDFSClient {

  def withLocalFileSystem(conf: YarnConfiguration): HDFSClient = {
    new HDFSClient(FileSystem.getLocal(conf))
  }

  def withDistributedFileSystem(hostPort: Option[HostPort], conf: YarnConfiguration): HDFSClient = {
    require(hostPort.isDefined, "HDFS host and port are not defined in config.")
    new HDFSClient(FileSystem.get(hostPort.get.getURI, conf))
  }
}
