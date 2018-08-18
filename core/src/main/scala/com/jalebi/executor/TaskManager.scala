package com.jalebi.executor

import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement.{ExecutorState, TaskRequest, TaskType}

case class TaskManager(executorId: String) {

  private var executorState: ExecutorState = NEW
  private var currentDataset: Option[String] = None

  def markRegistered(): Unit = {
    executorState = REGISTERED
  }

  def execute(taskRequest: TaskRequest): Unit = {
    taskRequest.taskType match {
      case TaskType.LOAD_DATASET => {
        require(taskRequest.dataset != null)
        require(taskRequest.parts.nonEmpty)

      }
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
