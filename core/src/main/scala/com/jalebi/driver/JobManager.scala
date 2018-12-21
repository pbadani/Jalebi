package com.jalebi.driver

import com.jalebi.api.{Vertex, VertexID}
import com.jalebi.common.{Logging, ResultConverter}
import com.jalebi.context.{Dataset, JalebiContext}
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.LocalScheduler
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.proto.jobmanagement.TaskResponse
import com.jalebi.yarn.ApplicationMaster

import scala.collection.mutable

case class JobManager(context: JalebiContext) extends Logging {

  private val applicationId = s"Jalebi-${System.currentTimeMillis()}"
  val executorState = ExecutorStateManager(context.conf)
  private val scheduler = if (context.onLocalMaster) LocalScheduler(context, executorState, applicationId) else ApplicationMaster(context, executorState, applicationId)
  private val driver = Driver(this, context.conf)
  val resultAggregator = new ResultAggregator(executorState)

  def ensureInitialized(): Unit = synchronized {
    if (!executorState.isInitialized) {
      driver.start()
      val executorIds = executorState.listExecutorIds()
      if (executorIds.isEmpty) {
        throw new IllegalStateException("No executors to run.")
      }
      LOGGER.info(s"Starting executors: [${executorIds.mkString(", ")}]")
      scheduler.startExecutors(executorIds)
      executorState.initialize()
    }
  }

  @throws[DatasetNotLoadedException]
  def ensureDatasetLoaded(name: String): Unit = {
    if (!context.isLoaded) {
      throw new DatasetNotLoadedException("No dataset is loaded currently.")
    }
    if (context.getCurrentDatasetName != name) {
      throw new DatasetNotLoadedException(s"Currently loaded dataset ${context.getCurrentDatasetName} is not same as $name")
    }
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Dataset = {
    ensureInitialized()
    val jobId = context.newJobId(applicationId)
    val parts = hdfsClient.listDatasetParts(name)
    executorState.loadPartsToExecutors(jobId, parts, name)
    resultAggregator.waitForJobToBeCompleted(jobId)
    Dataset(name, this)
  }

  def findVertex(vertexId: VertexID, name: String): mutable.Queue[Vertex] = {
    ensureDatasetLoaded(name)
    val jobId = context.newJobId(applicationId)
    executorState.assignNewTask(TaskRequestBuilder.searchRequest(jobId, vertexId, name))
    val responseToVertexes: TaskResponse => Seq[Vertex] = response => ResultConverter.convertFromVertices(response.vertexResults)
    resultAggregator.getResultForJobId(jobId, responseToVertexes)
  }

  def close(): Unit = {
    LOGGER.info("Shutting all executors.")
    scheduler.shutAllExecutors()
    driver.interrupt()
  }

  def driverHostPort: RichHostPort = context.driverHostPort
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
