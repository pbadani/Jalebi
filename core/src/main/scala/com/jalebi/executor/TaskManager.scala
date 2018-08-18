package com.jalebi.executor

import com.jalebi.api.Jalebi
import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement.{ExecutorState, TaskRequest, TaskType}
import com.jalebi.utils.Logging

case class TaskManager(executorId: String) extends Logging {

  private var executorState: ExecutorState = NEW
  private var currentDataset: Option[String] = None
  private var currentJalebi: Option[Jalebi] = None
  private var running = true
  private var taskConfig: Option[TaskConfig] = None

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
    taskRequest.taskType match {
      case TaskType.LOAD_DATASET =>
        require(taskRequest.dataset != null)
        require(taskRequest.parts.nonEmpty)
        require(currentState != NEW && currentState != UNREGISTERED)
        LOGGER.info(s"Request to load dataset ${taskRequest.dataset}")
        currentJalebi = Some(loadDataset(taskRequest.dataset, taskRequest.parts.toSet))
      case TaskType.BREADTH_FIRST => {
        require(currentDataset.isDefined)
      }
      case TaskType.DEPTH_FIRST => {
        require(currentDataset.isDefined)
      }
    }
  }

  def currentState: ExecutorState = executorState

}
