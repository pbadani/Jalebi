package com.jalebi.hdfs

import com.jalebi.utils.Logging
import org.apache.hadoop.fs.{BlockLocation, FileSystem, Path}
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.collection.mutable.ListBuffer

case class HostPort(host: String, port: String) {
  def getAddress: String = s"$host:$port"
}

case class HDFSClient(fs: FileSystem) extends Logging {

  def checkDatasetExists(name: String): Boolean = {
    val location = s"${HDFSClientConstants.datasetParentDirectory}$name"
    LOGGER.info(s"Checking for Dataset $name at location $location for Filesystem $fs.")
    val fileExists = fs.exists(new Path(location))
    LOGGER.info(s"Dataset $name ${if (fileExists) "exists" else "doesn't exist."}")
    fileExists
  }

  def listDatasets(): Set[String] = {
    val files = new ListBuffer[String]()
    val location = HDFSClientConstants.datasetParentDirectory
    LOGGER.info(s"Listing Datasets at location $location for Filesystem $fs.")
    val fileIterator = fs.listFiles(new Path(location), false)
    while (fileIterator.hasNext)
      files += fileIterator.next().getPath.getName
    LOGGER.info(s"Datasets found for Filesystem $fs: [${files.mkString(", ")}]")
    files.toSet
  }

  def getFileBlockLocations(): Array[BlockLocation] = {
    val fileStatus = fs.getFileStatus(null)
    fs.getFileBlockLocations(fileStatus, 0L, fileStatus.getLen)
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
    conf.set("fs.defaultFS", s"hdfs://${hostPort.get.getAddress}")
    new HDFSClient(FileSystem.get(conf))
  }
}
