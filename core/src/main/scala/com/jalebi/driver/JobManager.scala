package com.jalebi.driver

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.context.JalebiContext
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.local.LocalScheduler
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler

case class JobManager(context: JalebiContext) extends Logging {

  val applicationId: String = s"Jalebi_App_${System.currentTimeMillis()}"
  private val jobIdCounter = new AtomicLong(0)
  private val executorIdCounter = new AtomicLong(0)
  private val numOfExecutors = context.conf.getNumberOfExecutors().toInt
  private val scheduler = if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  private val driverCoordinatorService = DriverCoordinatorService(this, context.conf)
  val executorState: ExecutorStateManager = {
    (0 until numOfExecutors)
      .foldLeft(ExecutorStateManager(context.conf))((acc, _) => acc.addExecutor(newExecutorId()))
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

  def newJobId(): String = s"${applicationId}_Job_${jobIdCounter.getAndIncrement()}"

  def newExecutorId(): String = s"${applicationId}_Executor_${executorIdCounter.getAndIncrement()}"
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
