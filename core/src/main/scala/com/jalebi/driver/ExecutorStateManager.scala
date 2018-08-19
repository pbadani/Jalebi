package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement.DatasetState.NONE
import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement.{DatasetState, ExecutorState, TaskType}
import com.jalebi.utils.Logging

import scala.collection.mutable

case class ExecutorStateManager(conf: JalebiConfig) extends Logging {

  case class State(parts: Set[String], executorState: ExecutorState, datasetState: DatasetState)

  private val default = State(Set.empty, NEW, NONE)

  private var initializationState = false

  private val nextAction = mutable.HashMap[String, TaskType]()

  private val executorIdToState = mutable.HashMap[String, State]().withDefaultValue(default)

  private val executorIdToLastHeartbeat = mutable.HashMap[String, Long]()

  def initialize(): Unit = {
    initializationState = true
  }

  def isInitialized: Boolean = initializationState

  def clearAndAssignPartsToExecutors(executorIdToParts: Map[String, Set[String]]): Unit = {
    clearParts()
    executorIdToParts.foreach {
      case (e, p) => assignPartsToExecutor(e, p)
    }
  }

  def assignPartsToExecutor(executorId: String, parts: Set[String]): Unit = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> state.copy(parts = state.parts ++ parts))
  }

  def removePartsFromExecutor(executorId: String, parts: Set[String]): Unit = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> state.copy(parts = state.parts -- parts))
  }

  def markRegistered(executorId: String): Unit = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> state.copy(executorState = REGISTERED))
  }

  def markUnregistered(executorId: String): Unit = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> state.copy(executorState = UNREGISTERED))
  }

  def updateLastHeartbeat(executorId: String, eState: ExecutorState, dState: DatasetState, heartbeat: Long): Unit = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> state.copy(executorState = eState, datasetState = dState))
    executorIdToLastHeartbeat(executorId) = heartbeat
  }

  def addExecutorId(executorId: String): ExecutorStateManager = {
    executorIdToState += (executorId -> default)
    this
  }

  def removeExecutorId(executorId: String): ExecutorStateManager = {
    executorIdToState.remove(executorId)
    this
  }

  def listExecutorIds(): Set[String] = executorIdToState.keySet.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = {
    executorIdToState(executorId).parts
  }

  def clearParts(): Unit = {
    executorIdToState.foreach {
      case (executorId, state) =>
        executorIdToState += (executorId -> state.copy(parts = Set.empty, datasetState = NONE))
    }
  }

  def getMissingHeartbeatExecutors: Set[String] = {

    def isOlderThan(lastHeartbeat: Long, current: Long): Boolean = {
      (current - lastHeartbeat) > ((conf.getHeartbeatInterval().toLong + conf.getHeartbeatMissBuffer().toLong) * 1000)
    }

    val current = System.currentTimeMillis()
    executorIdToLastHeartbeat.filter {
      case (executorId, lastHeartbeat) =>
        val missedHeartbeat = isOlderThan(lastHeartbeat, current)
        if (missedHeartbeat) {
          LOGGER.warn(s"Executor id $executorId missed heartbeat. Current time: $current, last heartbeat time: $lastHeartbeat")
        }
        missedHeartbeat
    }.toMap.keySet
  }
}
