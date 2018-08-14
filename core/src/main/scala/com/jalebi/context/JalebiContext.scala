package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api.{Triplet, Triplets}
import com.jalebi.exception.{DatasetNotFoundException, DatasetNotLoadedException}
import com.jalebi.hdfs.{HDFSClient, HostPort}
import com.jalebi.job.JobManager
import org.apache.hadoop.net.NetUtils

case class JalebiContext private(conf: JalebiConfig) {

  val applicationId = s"Jalebi_App_${System.currentTimeMillis()}"
  private val jobIdCounter = new AtomicLong(0)
  private val executorIdCounter = new AtomicLong(0)
  private var currentDataset: Option[String] = None
  val jobManager: JobManager = JobManager.createNew(this)

  val driverHostPort = HostPort(NetUtils.getLocalHostname, "8585")

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

  def createDataset(input: Inputter): Unit = {
    val verticesMap = input.listVertices.map(v => (v.id, v)).toMap
    val edges = input.listEdges
    val name = input.datasetName
    val triplets = edges.map(edge => {
      Triplet(verticesMap(edge.source), edge, verticesMap(edge.target))
    }).grouped(100)
      .map(t => Triplets(t))
      .toSeq
    HDFSClient.withDistributedFileSystem(conf.hdfsHostPort).createDataset(name, triplets)
  }

  def onLocalMaster: Boolean = conf.master == "local"

  def newJobId(): String = s"${applicationId}_Job_${jobIdCounter.getAndIncrement()}"

  def newExecutorId(): String = s"${applicationId}_Executor_${executorIdCounter.getAndIncrement()}"
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
