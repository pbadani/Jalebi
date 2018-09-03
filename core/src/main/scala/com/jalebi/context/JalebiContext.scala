package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api.{Triplet, Triplets}
import com.jalebi.common.Logging
import com.jalebi.driver.JobManager
import com.jalebi.exception._
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import org.apache.hadoop.net.NetUtils
import org.apache.hadoop.yarn.conf.YarnConfiguration

case class JalebiContext private(conf: JalebiConfig) extends Logging {

  private var currentDataset: Option[Dataset] = None
  val driverHostPort: RichHostPort = new RichHostPort("http", NetUtils.getLocalHostname, NetUtils.getFreeSocketPort)
  val yarnConf = new YarnConfiguration()
  private val jobIdCounter = new AtomicLong(0)
  private val executorIdCounter = new AtomicLong(0)
  // Let this context object be constructed before passing it to JobManager
  lazy val jobManager: JobManager = JobManager.createNew(this)

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

  def newJobId(applicationId: String): String = s"$applicationId-Job-${jobIdCounter.getAndIncrement()}"

  def newExecutorId(applicationId: String): String = s"$applicationId-Executor-${executorIdCounter.getAndIncrement()}"
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
