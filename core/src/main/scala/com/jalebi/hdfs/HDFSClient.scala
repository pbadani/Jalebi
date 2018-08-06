package com.jalebi.hdfs

import java.net.URI

import com.jalebi.utils.Logging
import org.apache.hadoop.fs.{BlockLocation, FileSystem, Path}
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.collection.mutable.ListBuffer

case class HostPort(host: String, port: String) {
  def getAddress: String = s"$host:$port"
}

case class HDFSClient(fs: FileSystem) extends Logging {

  //  def withDataset[U](name: String)(f: FileStatus => U): U = {
  //    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
  //    fs.getFileStatus(new Path(filePath))
  //    val datasetExists =
  //  }

  def checkDatasetExists(name: String): Boolean = {
    val filePath = s"${HDFSClientConstants.datasetParentDirectory}$name"
    LOGGER.info(s"Checking for Dataset $name at location $filePath for Filesystem $fs.")
    val fileExists = fs.exists(new Path(filePath))
    LOGGER.info(s"Dataset $name ${if (fileExists) "exists." else "doesn't exist."}")
    fileExists
  }

  def listDatasets(): Set[String] = {
    val files = new ListBuffer[String]()
    val filePath = HDFSClientConstants.datasetParentDirectory
    LOGGER.info(s"Listing Datasets at location $filePath for Filesystem $fs.")
    val fileIterator = fs.listFiles(new Path(filePath), false)
    while (fileIterator.hasNext)
      files += fileIterator.next().getPath.getName
    LOGGER.info(s"Datasets found for Filesystem $fs: [${files.mkString(", ")}]")
    files.toSet
  }

  def getFileBlockLocations(name: String): Array[BlockLocation] = {
    val path = new Path(s"${HDFSClientConstants.datasetParentDirectory}$name")
    val fileStatus = fs.getFileStatus(path)
    fs.getFileBlockLocations(path, 0L, fileStatus.getLen)
  }
}

object HDFSClient {

  def withLocalFileSystem(): HDFSClient = {
    val conf = new YarnConfiguration()
    new HDFSClient(FileSystem.getLocal(conf))
  }

  def withDistributedFileSystem(hostPort: Option[HostPort]): HDFSClient = {
    require(hostPort.isDefined, "HDFS host and port are not defined in config.")
    val conf = new YarnConfiguration()
    new HDFSClient(FileSystem.get(new URI(s"hdfs://${hostPort.get.getAddress}"), conf))
  }
}
