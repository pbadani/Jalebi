package com.jalebi.hdfs

import java.net.URI

import com.jalebi.api.{Jalebi, Triplets}
import com.jalebi.exception.{DatasetCorruptException, DatasetNotFoundException, DuplicateDatasetException}
import com.jalebi.proto.jobmanagement.HostPort
import com.jalebi.utils.Logging
import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.collection.mutable.ListBuffer

case class HDFSClient(fs: FileSystem) extends Logging {

  @throws[DuplicateDatasetException]
  def createDataset(name: String, triplets: => Iterator[Triplets]): Unit = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    if (doesDatasetExists(name)) {
      throw new DuplicateDatasetException(s"Dataset '$name' is already present at $filePath.")
    }
    LOGGER.info(s"Creating Dataset '$name' at location $filePath.")
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
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    val missingParts = parts.map(part => !existingParts.contains(part))
    if (missingParts.nonEmpty) {
      throw new DatasetCorruptException(s"Missing parts ${missingParts.mkString(", ")}")
    }
    Jalebi(name, parts.flatMap(partFileName => {
      val inputStream = AvroInputStream.data[Triplets](Seq(filePath, partFileName).mkString("/"))
      LOGGER.info(s"Reading $partFileName for Dataset '$name' at $filePath.")
      val triplets = inputStream.iterator.toSet
      inputStream.close()
      triplets
    }))
  }

  @throws[DatasetNotFoundException]
  def ensureDatasetExists(name: String): Unit = {
    doesDatasetExists(name, Some(throw new DatasetNotFoundException(s"Dataset '$name' not found.")))
  }

  def doesDatasetExists(name: String, actionIfNotExists: => Option[() => Unit] = None): Boolean = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    val fileExists = fs.exists(new Path(filePath))
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
      if (files.isEmpty) s"No Dataset found at $filePath" else s"Datasets found: [${files.mkString(", ")}]"
    }")
    files
  }

  @throws[DatasetNotFoundException]
  def listDatasetParts(name: String): Set[String] = {
    ensureDatasetExists(name)
    LOGGER.info(s"Listing parts for Dataset $name.")
    listDirectory(new Path(s"${HDFSClientConstants.datasetParentDirectory}$name"))
  }

  private def listDirectory(path: Path): Set[String] = {
    val fileIterator = fs.listFiles(path, false)
    val file = ListBuffer[String]()
    while (fileIterator.hasNext)
      file += fileIterator.next().getPath.getName
    file.toSet
  }
}

object HDFSClient {

  implicit class RichHostPort(hostPort: HostPort) {

    def this(host: String, port: Long) = this(HostPort(host, port))

    def getAddress: String = s"$host:$port"

    def getHDFSPath: String = s"hdfs://$getAddress"

    def port: Long = hostPort.port

    def host: String = hostPort.host

    def toHostPort: HostPort = HostPort(host, port)
  }

  def withLocalFileSystem(): HDFSClient = {
    new HDFSClient(FileSystem.getLocal(new YarnConfiguration()))
  }

  def withDistributedFileSystem(hostPort: Option[RichHostPort]): HDFSClient = {
    require(hostPort.isDefined, "HDFS host and port are not defined in config.")
    new HDFSClient(FileSystem.get(new URI(hostPort.get.getHDFSPath), new YarnConfiguration()))
  }
}
