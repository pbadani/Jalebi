package com.jalebi.driver

import com.jalebi.context.JalebiContext
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.local.LocalScheduler
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.proto.jobmanagement.TaskRequest
import com.jalebi.proto.jobmanagement.TaskType.LOAD_DATASET
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler

case class JobManager(context: JalebiContext) extends Logging {

  private val numOfExecutors = context.conf.getNumberOfExecutors().toInt
  private val scheduler = if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  private val driverCoordinatorService = DriverCoordinatorService(this, context.conf)
  val executorState: ExecutorStateManager = {
    (0 until numOfExecutors)
      .foldLeft(ExecutorStateManager(context.conf))((acc, _) => acc.addExecutor(context.newExecutorId()))
  }

  def ensureInitialized(): Unit = synchronized {
    if (!executorState.isInitialized) {
      driverCoordinatorService.start()
      LOGGER.info(s"Starting executors: [${executorState.listExecutorIds().mkString(", ")}]")
      scheduler.startExecutors(executorState.listExecutorIds())
      executorState.initialize()
    }
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Boolean = {
    ensureInitialized()
    val parts = hdfsClient.listDatasetParts(name)
    val executors = executorState.listExecutorIds()
    val executorIdToParts = HashPartitioner.partition(parts, executors)
    executorState.clearAndAssignPartsToExecutors(executorIdToParts, name)
    true
  }

  def shutRunningExecutors(): Unit = {
    LOGGER.info("Shutting all executors.")
    scheduler.shutAllExecutors()
  }

  def driverHostPort: RichHostPort = context.driverHostPort
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
