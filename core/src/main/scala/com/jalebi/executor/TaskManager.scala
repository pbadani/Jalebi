package com.jalebi.executor

import com.jalebi.proto.jobmanagement.ExecutorState

case class TaskManager(executorId: String) {
  private val localStateManager = LocalStateManager()

  def markRegistered(): Unit = localStateManager.markRegistered()

  def currentState: ExecutorState = localStateManager.currentState

}
