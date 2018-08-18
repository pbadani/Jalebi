package com.jalebi.driver

import com.jalebi.context.JalebiContext
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.standalone.LocalScheduler
import com.jalebi.hdfs.{HDFSClient, HostPort}
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler

case class JobManager(context: JalebiContext) extends Logging {

  private val numOfExecutors = context.conf.options.getNumberOfExecutors().toInt
  private val scheduler = if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  private val driverCoordinatorService = DriverCoordinatorService(this, context.conf)
  val executorState: ExecutorState = {
    (0 until numOfExecutors)
      .foldLeft(ExecutorState())((acc, _) => acc.addExecutorId(context.newExecutorId()))
  }

  def ensureInitialized(): Unit = synchronized {
    if (!executorState.isInitalized) {
      driverCoordinatorService.start()
      LOGGER.info(s"Starting executors: [${executorState.listExecutorIds().mkString(", ")}]")
      scheduler.startExecutors(executorState.listExecutorIds())
      executorState.initalize()
    }
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Boolean = {
    ensureInitialized()
    val parts = hdfsClient.listDatasetParts(name)
    val executors = executorState.listExecutorIds()
    val executorIdToParts = HashPartitioner.partition(parts, executors)

    true
  }

  def shutRunningExecutors(): Unit = {
    LOGGER.info(s"Shutting All executors.")
    scheduler.shutAllExecutors()
  }

  def driverHostPort: HostPort = context.driverHostPort
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
