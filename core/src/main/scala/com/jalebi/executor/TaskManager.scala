package com.jalebi.executor

import com.jalebi.api.{Jalebi, VertexID}
import com.jalebi.common.{Logging, ResultConverter}
import com.jalebi.exception.DatasetCorruptException
import com.jalebi.proto.jobmanagement.DatasetState._
import com.jalebi.proto.jobmanagement.TaskState._
import com.jalebi.proto.jobmanagement._
import org.apache.commons.lang.StringUtils

import scala.collection.mutable

case class TaskManager(executorId: String) extends Logging {

  private var datasetState: DatasetState = NONE
  private var executorState: ExecutorState = New
  private var currentJalebi: Option[Jalebi] = None
  private var running = true
  private var taskConfig: Option[TaskConfig] = None
  val propagateInHeartbeat = PropagateInHeartbeat(this)

  case class PropagateInHeartbeat(taskManager: TaskManager) {
    private val queue = new mutable.Queue[TaskResponse]()

    def put(taskResponse: TaskResponse): Unit = queue.enqueue(taskResponse)

    def get: TaskResponse = {
      if (queue.nonEmpty) {
        val resp = queue.dequeue()
        LOGGER.info(s"Propagating response $resp")
        resp
      } else {
        TaskResponse(StringUtils.EMPTY, StringUtils.EMPTY, RUNNING, taskManager.executorId, null, taskManager.datasetState)
      }
    }
  }

  def keepRunning: Boolean = running

  def heartbeatInterval: Long = {
    taskConfig.map(conf => conf.heartbeatInterval).getOrElse(5)
  }

  def markRegistered(taskConfig: TaskConfig): Unit = {
    executorState = Registered
    LOGGER.info(s"Task config - $taskConfig")
    this.taskConfig = Some(taskConfig)
  }

  def markUnregistered(taskConfig: TaskConfig): Unit = {
    executorState = Unregistered
    LOGGER.info(s"Task config - $taskConfig")
    this.taskConfig = Some(taskConfig)
  }

  def loadDataset(dataset: String, parts: Set[String]): Jalebi = {
    taskConfig.get.hdfsClient.loadDataset(dataset, parts)
  }

  def execute(taskRequest: TaskRequest): Unit = {
    require(executorState != New || executorState != Unregistered || executorState != AssumedDead)
    taskRequest.taskType match {
      case TaskType.LOAD_DATASET =>
        require(taskRequest.dataset != null, "Dataset name cannot be null.")
        require(taskRequest.parts.nonEmpty, "No parts to load.")
        require(executorState != New && executorState != Unregistered, s"Executor $executorId should not be in $executorState.")
        try {
          LOGGER.info(s"Loading '${taskRequest.dataset}' parts [${taskRequest.parts.mkString(", ")}] on $executorId.")
          setStates(datasetState = Some(LOADING), executorState = Some(RunningJob))
          currentJalebi = Some(loadDataset(taskRequest.dataset, taskRequest.parts.toSet))
          setStates(datasetState = Some(LOADED), executorState = Some(Runnable))
          val response = TaskResponse(taskRequest.jobId, taskRequest.taskId, COMPLETED, executorId, null, datasetState)
          LOGGER.info(s"Load dataset response $response on $executorId.")
          propagateInHeartbeat.put(response)
        } catch {
          case e: DatasetCorruptException =>
            currentJalebi = None
            setStates(datasetState = Some(NONE), executorState = Some(Runnable))
            propagateInHeartbeat.put(TaskResponse(taskRequest.jobId, taskRequest.taskId, FAILED, executorId, null, datasetState))
            LOGGER.error(e.getMessage)
        }

      case TaskType.SEARCH_VERTEX =>
        require(currentJalebi.isDefined)
        require(currentJalebi.get.name == taskRequest.dataset, s"Dataset mismatch. Expected ${currentJalebi.get.name}, Actual: ${taskRequest.dataset}")
        LOGGER.info(s"Searching vertex '${taskRequest.startVertexId} on $executorId.")
        setStates(None, executorState = Some(RunningJob))
        val result = currentJalebi.get.searchVertex(VertexID(taskRequest.startVertexId))
        val vertexResult = ResultConverter.convertToVertexResult(result)
        val response = TaskResponse(taskRequest.jobId, taskRequest.taskId, COMPLETED, executorId, null, datasetState, vertexResult, Nil)
        LOGGER.info(s"Search vertex response $response on $executorId.")
        propagateInHeartbeat.put(response)
        setStates(None, executorState = Some(Runnable))

      case TaskType.BREADTH_FIRST =>
        require(currentJalebi.isDefined)
      case TaskType.DEPTH_FIRST =>
        require(currentJalebi.isDefined)

      case _ =>
        throw new IllegalStateException()
    }
  }

  private def setStates(datasetState: Option[DatasetState] = None, executorState: Option[ExecutorState] = None): Unit = {
    datasetState.foreach(state => this.datasetState = state)
    executorState.foreach(state => this.executorState = state)
  }
}
