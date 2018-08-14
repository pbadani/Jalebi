package com.jalebi.hdfs

import java.net.URI

import com.jalebi.api.Triplets
import com.jalebi.exception.{DatasetNotFoundException, DuplicateDatasetException}
import com.jalebi.utils.Logging
import com.sksamuel.avro4s.{AvroOutputStream, AvroSchema}
import org.apache.avro.Schema
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.collection.mutable.ListBuffer

case class HostPort(host: String, port: String) {
  def getAddress: String = s"$host:$port"
}

case class HDFSClient(fs: FileSystem) extends Logging {

  @throws[DuplicateDatasetException]
  def createDataset(name: String, triplets: Seq[Triplets]): Unit = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    LOGGER.info(s"Creating Dataset '$name' at location $filePath.")
    if (checkDatasetExists(name)) {
      throw new DuplicateDatasetException(s"Dataset '$name' is already present at $filePath.")
    }
    triplets.zipWithIndex.foreach {
      case (t, index) =>
        val partFileName = s"part-$index"
        val os = fs.create(new Path(Seq(filePath, partFileName).mkString("/")))
        val outputStream = AvroOutputStream.data[Triplets](os)
        LOGGER.info(s"Writing part $partFileName for Dataset '$name' at $filePath.")
        outputStream.write(t)
        outputStream.flush()
        outputStream.close()
    }
  }

  @throws[DatasetNotFoundException]
  def ensureDatasetExists(name: String): Unit = {
    if (!checkDatasetExists(name)) {
      throw new DatasetNotFoundException(s"Dataset '$name' not found.")
    }
  }

  def checkDatasetExists(name: String): Boolean = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    LOGGER.info(s"Checking for Dataset $name at location $filePath.")
    val fileExists = fs.exists(new Path(filePath))
    LOGGER.info(s"Dataset $name ${if (fileExists) "exists." else "doesn't exist."}")
    fileExists
  }

  def listDatasets(): Set[String] = {
    val filePath = new Path(HDFSClientConstants.datasetParentDirectory)
    LOGGER.info(s"Listing Datasets at $filePath.")
    val files = listDirectory(filePath)
    LOGGER.info(s"${
      if (files.isEmpty)
        s"No Dataset found at $filePath"
      else
        s"Datasets found: [${files.mkString(", ")}]"
    }")
    files
  }

  @throws[DatasetNotFoundException]
  def listDatasetParts(name: String): Set[String] = {
    if (!checkDatasetExists(name)) {
      throw new DatasetNotFoundException(s"Dataset '$name' not found.")
    }
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

  def withLocalFileSystem(): HDFSClient = {
    new HDFSClient(FileSystem.getLocal(new YarnConfiguration()))
  }

  def withDistributedFileSystem(hostPort: Option[HostPort]): HDFSClient = {
    require(hostPort.isDefined, "HDFS host and port are not defined in config.")
    new HDFSClient(FileSystem.get(new URI(s"hdfs://${hostPort.get.getAddress}"), new YarnConfiguration()))
  }
}
