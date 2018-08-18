package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api.{Triplet, Triplets}
import com.jalebi.driver.JobManager
import com.jalebi.exception.{DatasetNotFoundException, DatasetNotLoadedException, DuplicateDatasetException}
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.proto.jobmanagement.HostPort
import org.apache.hadoop.net.NetUtils

case class JalebiContext private(conf: JalebiConfig) {

  val applicationId = s"Jalebi_App_${System.currentTimeMillis()}"
  private val jobIdCounter = new AtomicLong(0)
  private val executorIdCounter = new AtomicLong(0)
  private var currentDataset: Option[String] = None
  val jobManager: JobManager = JobManager.createNew(this)

  val driverHostPort = new RichHostPort(NetUtils.getLocalHostname, 8585)

  @throws[DatasetNotFoundException]
  @throws[DatasetNotLoadedException]
  def loadDataset[V, E](name: String): Unit = {
    val hdfsClient = HDFSClient.withDistributedFileSystem(conf.hdfsHostPort)
    if (!hdfsClient.checkDatasetExists(name)) {
      throw new DatasetNotFoundException(s"Dataset '$name' not found.")
    }
    if (jobManager.load(hdfsClient, name)) {
      currentDataset = Some(name)
    }
  }

  @throws[DuplicateDatasetException]
  def createDataset(input: JalebiWriter): Unit = {
    val verticesMap = input.vertices.map(v => (v.id, v)).toMap
    val triplets = input.edges.map(edge => {
      Triplet(verticesMap(edge.source), edge, verticesMap(edge.target))
    }).grouped(conf.options.getPartitionSize().toInt)
      .map(Triplets(_))
    HDFSClient.withDistributedFileSystem(conf.hdfsHostPort).createDataset(input.datasetName, triplets)
  }

  def onLocalMaster: Boolean = conf.master == "local"

  def newJobId(): String = s"${applicationId}_Job_${jobIdCounter.getAndIncrement()}"

  def newExecutorId(): String = s"${applicationId}_Executor_${executorIdCounter.getAndIncrement()}"
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
