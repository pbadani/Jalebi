package com.jalebi.driver

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api.{Vertex, VertexID}
import com.jalebi.common.Logging
import com.jalebi.context.{Dataset, JalebiContext}
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.local.LocalScheduler
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.yarn.YarnScheduler

case class JobManager(context: JalebiContext) extends Logging {

  val applicationId: String = s"Jalebi-${System.currentTimeMillis()}"
  private val jobIdCounter = new AtomicLong(0)
  private val executorIdCounter = new AtomicLong(0)
  private val numOfExecutors = context.conf.getNumberOfExecutors().toInt
  private val scheduler = if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  private val driverCoordinatorService = DriverCoordinatorService(this, context.conf)
  val resultAggregator = new ResultAggregator()
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
  def ensureDatasetLoaded(name: String): Unit = {
    if (!context.isLoaded) {
      throw new DatasetNotLoadedException(s"No dataset is loaded currently.")
    }
    if (context.getCurrentDatasetName != name) {
      throw new DatasetNotLoadedException(s"Currently loaded dataset ${context.getCurrentDatasetName} is not same as $name")
    }
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Dataset = {
    ensureInitialized()
    val parts = hdfsClient.listDatasetParts(name)
    val executors = executorState.listExecutorIds()
    val executorIdToParts = HashPartitioner.partition(parts, executors)
    executorState.clearAndAssignPartsToExecutors(newJobId(), executorIdToParts, name)
    Dataset(name, this)
  }


  def findVertex(vertexId: VertexID, name: String): Set[Vertex] = {
    ensureDatasetLoaded(name)
    val jobId = newJobId()
    val request = TaskRequestBuilder.searchRequest(jobId, vertexId, name)
    executorState.assignNewTask(request)
    resultAggregator.getResultForJobId(jobId, response => response.vertexResults)
  }

  def shutRunningExecutors(): Unit = {
    LOGGER.info("Shutting all executors.")
    scheduler.shutAllExecutors()
  }

  def driverHostPort: RichHostPort = context.driverHostPort

  def newJobId(): String = s"$applicationId-Job-${jobIdCounter.getAndIncrement()}"

  def newExecutorId(): String = s"$applicationId-Executor-${executorIdCounter.getAndIncrement()}"
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
