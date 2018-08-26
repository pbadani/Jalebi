package com.jalebi.executor

import com.jalebi.api.{Jalebi, VertexID}
import com.jalebi.exception.DatasetCorruptException
import com.jalebi.proto.jobmanagement.DatasetState._
import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement._
import com.jalebi.utils.Logging

import scala.collection.mutable

case class TaskManager(executorId: String) extends Logging {

  private var datasetState: DatasetState = NONE
  private var executorState: ExecutorState = NEW
  private var currentJalebi: Option[Jalebi] = None
  private var running = true
  private var taskConfig: Option[TaskConfig] = None
  val propagateInHeartbeat = PropagateInHeartbeat(this)

  case class PropagateInHeartbeat(taskManager: TaskManager) {
    private val queue = new mutable.Queue[TaskResponse]()

    def put(taskResponse: TaskResponse): Unit = queue.enqueue(taskResponse)

    def get: TaskResponse = {
      if (queue.nonEmpty) {
        queue.dequeue()
      } else {
        TaskResponse("", taskManager.executorId, taskManager.executorState, taskManager.datasetState)
      }
    }
  }

  def keepRunning: Boolean = running

  def heartbeatInterval: Long = {
    if (taskConfig.isDefined) {
      taskConfig.get.heartbeatInterval
    } else {
      5
    }
  }

  def markRegistered(taskConfig: TaskConfig): Unit = {
    executorState = REGISTERED
    LOGGER.info(s"Task config - $taskConfig")
    this.taskConfig = Some(taskConfig)
  }

  def loadDataset(dataset: String, parts: Set[String]): Jalebi = {
    taskConfig.get.hdfsClient.loadDataset(dataset, parts)
  }

  def execute(taskRequest: TaskRequest): Unit = {
    require(executorState != NEW || executorState != UNREGISTERED || executorState != ASSUMED_DEAD)
    taskRequest.taskType match {
      case TaskType.LOAD_DATASET =>
        require(taskRequest.dataset != null, "Dataset name cannot be null.")
        require(taskRequest.parts.nonEmpty, "No parts to load.")
        require(executorState != NEW && executorState != UNREGISTERED, s"Executor should not be in $executorState.")
        try {
          LOGGER.info(s"Loading dataset ${taskRequest.dataset}, parts [${taskRequest.parts.mkString(", ")}]")
          setStates(datasetState = Some(LOADING), executorState = Some(RUNNING_JOB))
          currentJalebi = Some(loadDataset(taskRequest.dataset, taskRequest.parts.toSet))
          setStates(datasetState = Some(LOADED), executorState = Some(RUNNABLE))
        } catch {
          case e: DatasetCorruptException =>
            currentJalebi = None
            setStates(datasetState = Some(NONE), executorState = Some(RUNNABLE))
            propagateInHeartbeat.put(TaskResponse("", executorId, executorState, datasetState))
            LOGGER.error(e.getMessage)
        }

      case TaskType.SEARCH_VERTEX =>
        require(currentJalebi.isDefined)
        require(currentJalebi.get.name == taskRequest.dataset, s"Dataset mismatch. Expected ${currentJalebi.get.name}, Actual: ${taskRequest.dataset}")
        setStates(None, executorState = Some(RUNNING_JOB))
        val result = currentJalebi.get.searchVertex(VertexID(taskRequest.startVertexId))

      case TaskType.BREADTH_FIRST =>
        require(currentJalebi.isDefined)
      case TaskType.DEPTH_FIRST =>
        require(currentJalebi.isDefined)
    }
  }

  private def setStates(datasetState: Option[DatasetState] = None, executorState: Option[ExecutorState] = None): Unit = {
    if (datasetState.isDefined) {
      this.datasetState = datasetState.get
    }
    if (executorState.isDefined) {
      this.executorState = executorState.get
    }
  }
}
