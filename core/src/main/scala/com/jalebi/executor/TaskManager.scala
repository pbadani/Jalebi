package com.jalebi.executor

import com.jalebi.api.Jalebi
import com.jalebi.exception.DatasetCorruptException
import com.jalebi.proto.jobmanagement.DatasetState.{LOADED, LOADING, NONE}
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
          LOGGER.info(s"Loading dataset ${taskRequest.dataset}")
          setStates(datasetState = LOADING, executorState = RUNNING_JOB)
          currentJalebi = Some(loadDataset(taskRequest.dataset, taskRequest.parts.toSet))
          setStates(datasetState = LOADED, executorState = RUNNABLE)
        } catch {
          case e: DatasetCorruptException =>
            currentJalebi = None
            setStates(datasetState = NONE, executorState = RUNNABLE)
            propagateInHeartbeat.put(TaskResponse("", executorId, executorState, datasetState))
            LOGGER.error(e.getMessage)
        }

      case TaskType.BREADTH_FIRST =>
        require(currentJalebi.isDefined)
      case TaskType.DEPTH_FIRST =>
        require(currentJalebi.isDefined)
    }
  }

  private def setStates(datasetState: DatasetState, executorState: ExecutorState): Unit = {
    this.datasetState = datasetState
    this.executorState = executorState
  }
}
