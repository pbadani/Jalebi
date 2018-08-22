package com.jalebi.hdfs

import java.io.ByteArrayOutputStream
import java.net.URI

import com.jalebi.api.{Jalebi, Triplets}
import com.jalebi.exception.{DatasetCorruptException, DatasetNotFoundException, DuplicateDatasetException}
import com.jalebi.proto.jobmanagement.HostPort
import com.jalebi.utils.Logging
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.collection.mutable.ListBuffer

case class HDFSClient(fs: FileSystem) extends Logging {

  @throws[DuplicateDatasetException]
  def createDataset(name: String, triplets: => Iterator[Triplets]): Unit = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    if (doesDatasetExists(name)) {
      throw new DuplicateDatasetException(s"Dataset '$name' is already present at $filePath.")
    }
    LOGGER.info(s"Creating Dataset '$name' at $filePath.")
    triplets.zipWithIndex.foreach {
      case (t, index) =>
        val partFileName = s"part-$index"
        val os = fs.create(new Path(Seq(filePath, partFileName).mkString("/")))
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
      val is = fs.open(new Path(Seq(filePath, partFileName).mkString("/")))
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
    doesDatasetExists(name, Some(throw new DatasetNotFoundException(s"Dataset '$name' not found.")))
  }

  def doesDatasetExists(name: String, actionIfNotExists: => Option[() => Unit] = None): Boolean = {
    val fileExists = fs.exists(new Path(s"${HDFSClientConstants.datasetParentDirectory}$name"))
    LOGGER.info(s"Dataset '$name' ${if (fileExists) "exists." else "doesn't exist."}")
    if (!fileExists && actionIfNotExists.isDefined) {
      actionIfNotExists.get()
    }
    fileExists
  }

  def listDatasets(): Set[String] = {
    val filePath = new Path(HDFSClientConstants.datasetParentDirectory)
    val files = listDirectory(filePath)
    LOGGER.info(s"Listing Datasets at $filePath : ${
      if (files.isEmpty) s"No Dataset found." else s"Datasets found: [${files.mkString(", ")}]."
    }")
    files
  }

  @throws[DatasetNotFoundException]
  def listDatasetParts(name: String): Set[String] = {
    ensureDatasetExists(name)
    val parts = listDirectory(new Path(s"${HDFSClientConstants.datasetParentDirectory}$name"))
    LOGGER.info(s"Listing parts for Dataset '$name' - [${parts.mkString(", ")}].")
    parts
  }

  def deleteDataset(name: String): Unit = {
    if (doesDatasetExists(name)) {
      LOGGER.info(s"Deleting dataset $name.")
      fs.delete(new Path(s"${HDFSClientConstants.datasetParentDirectory}$name"), true)
    }
  }

  private def listDirectory(path: Path): Set[String] = {
    val fileIterator = fs.listFiles(path, false)
    val file = ListBuffer[String]()
    while (fileIterator.hasNext)
      file += fileIterator.next().getPath.getName
    file.toSet
  }

  def deleteDirectory: Unit = {
    fs.delete(new Path(HDFSClientConstants.datasetParentDirectory), true)
  }
}

object HDFSClient {

  implicit class RichHostPort(hostPort: HostPort) {

    def this(scheme: String, host: String, port: Long) = this(HostPort(scheme, host, port))

    def getURI: URI = new URI(s"$scheme://$host:$port")

    def port: Long = hostPort.port

    def host: String = hostPort.host

    def scheme: String = hostPort.scheme

    def toHostPort: HostPort = HostPort(scheme, host, port)
  }

  //  def fileSystem(conf: JalebiConfig): HDFSClient = {
  //
  //  }

  def withLocalFileSystem(): HDFSClient = {
    new HDFSClient(FileSystem.getLocal(new YarnConfiguration()))
  }

  def withDistributedFileSystem(hostPort: Option[RichHostPort]): HDFSClient = {
    require(hostPort.isDefined, "HDFS host and port are not defined in config.")
    new HDFSClient(FileSystem.get(hostPort.get.getURI, new YarnConfiguration()))
  }
}
