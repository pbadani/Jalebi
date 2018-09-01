package com.jalebi.context

import com.jalebi.api.{Triplet, Triplets}
import com.jalebi.driver.JobManager
import com.jalebi.exception._
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import org.apache.hadoop.net.NetUtils
import org.apache.hadoop.yarn.conf.YarnConfiguration

case class JalebiContext private(conf: JalebiConfig) {

  private var currentDataset: Option[Dataset] = None
  val driverHostPort: RichHostPort = new RichHostPort("http", NetUtils.getLocalHostname, 8585)
  val jobManager: JobManager = JobManager.createNew(this)
  val yarnConf = new YarnConfiguration()

  @throws[DatasetNotFoundException]
  @throws[DatasetNotLoadedException]
  def loadDataset(name: String): Dataset = {
    val hdfsClient = HDFSClient.withDistributedFileSystem(conf.hdfsHostPort, yarnConf)
    if (!hdfsClient.doesDatasetExists(name)) {
      throw new DatasetNotFoundException(s"Dataset '$name' not found.")
    }
    currentDataset = Some(jobManager.load(hdfsClient, name))
    currentDataset.get
  }

  def deleteDataset(name: String): Unit = {
    HDFSClient.withDistributedFileSystem(conf.hdfsHostPort, yarnConf).deleteDataset(name)
  }

  @throws[DuplicateDatasetException]
  def createDataset(input: JalebiWriter): Unit = {
    val verticesMap = input.vertices.map(v => (v.vertexId, v)).toMap
    val triplets = input.edges.map(edge => {
      Triplet(verticesMap(edge.source), edge, verticesMap(edge.target))
    }).grouped(conf.options.getPartitionSize().toInt)
      .map(Triplets(_))
    HDFSClient.withDistributedFileSystem(conf.hdfsHostPort, yarnConf).createDataset(input.datasetName, triplets)
  }

  def onLocalMaster: Boolean = conf.master == "local"

  def getCurrentDatasetName: String = currentDataset.get.name

  def isLoaded: Boolean = currentDataset.isDefined
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
