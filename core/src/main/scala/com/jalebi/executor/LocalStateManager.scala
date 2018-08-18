package com.jalebi.executor

import com.jalebi.proto.jobmanagement.ExecutorState
import com.jalebi.proto.jobmanagement.ExecutorState._

case class LocalStateManager() {

  private var executorState: ExecutorState = NEW

  def markRegistered(): Unit = {
    executorState = REGISTERED
  }

  def currentState: ExecutorState = executorState
}
