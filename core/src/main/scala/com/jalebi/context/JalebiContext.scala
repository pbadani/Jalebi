package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api._
import com.jalebi.exception.{DatasetNotFoundException, DatasetNotLoadedException}
import com.jalebi.hdfs.{FileSystemType, HDFSClient, HostPort}
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
  def load[V, E](name: String, fileSystem: FileSystemType.FS): Unit = {
    val hdfsClient = fileSystem match {
      case FileSystemType.HDFS_LOCAL => HDFSClient.withLocalFileSystem()
      case FileSystemType.HDFS_DISTRIBUTED => HDFSClient.withDistributedFileSystem(conf.hdfsHostPort)
    }
    if (!hdfsClient.checkDatasetExists(name)) {
      throw new DatasetNotFoundException(s"Dataset '$name' not found at $fileSystem filesystem.")
    }
    if (jobManager.load(hdfsClient, name)) {
      currentDataset = Some(name)
    }
  }

  def onLocalMaster: Boolean = conf.master == "local"

  def newJobId(): String = s"${applicationId}_Job_${jobIdCounter.getAndIncrement()}"

  def newExecutorId(): String = s"${applicationId}_Executor_${executorIdCounter.getAndIncrement()}"
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
