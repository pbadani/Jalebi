package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement.DatasetState.NONE
import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement.{DatasetState, ExecutorState, TaskType}
import com.jalebi.utils.Logging

import scala.collection.mutable

case class ExecutorStateManager(conf: JalebiConfig) extends Logging {

  case class State(parts: Set[String], executorState: ExecutorState, datasetState: DatasetState)

  private val default = State(parts = Set.empty, executorState = NEW, datasetState = NONE)

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
    LOGGER.info(s"Assigned parts ${parts.mkString(",")} to Executor $executorId")
    updateState(executorId, state => state.copy(parts = state.parts ++ parts))
  }

  def removePartsFromExecutor(executorId: String, parts: Set[String]): Unit = {
    LOGGER.info(s"Removed parts ${parts.mkString(",")} from Executor $executorId")
    updateState(executorId, state => state.copy(parts = state.parts -- parts))
  }

  def markRegistered(executorId: String): Unit = {
    LOGGER.info(s"Registering $executorId")
    updateState(executorId, state => state.copy(executorState = REGISTERED))
  }

  def markUnregistered(executorId: String): Unit = {
    LOGGER.info(s"Unregistering $executorId")
    updateState(executorId, state => state.copy(executorState = UNREGISTERED))
  }

  def updateLastHeartbeat(executorId: String, eState: ExecutorState, dState: DatasetState, heartbeat: Long): Unit = {
    updateState(executorId, state => state.copy(executorState = eState, datasetState = dState))
    executorIdToLastHeartbeat(executorId) = heartbeat
  }

  def addExecutorId(executorId: String): ExecutorStateManager = {
    LOGGER.info(s"Adding executor $executorId")
    executorIdToState += (executorId -> default)
    this
  }

  def removeExecutorId(executorId: String): ExecutorStateManager = {
    LOGGER.info(s"Removing executor $executorId")
    executorIdToState.remove(executorId)
    this
  }

  private def updateState(executorId: String, oldStateToNewState: State => State) = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> oldStateToNewState(state))
  }

  def listExecutorIds(): Set[String] = executorIdToState.keySet.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = {
    executorIdToState(executorId).parts
  }

  def clearParts(): Unit = {
    executorIdToState.mapValues(state => state.copy(parts = Set.empty, datasetState = NONE))
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
          LOGGER.warn(s"Executor id $executorId missed heartbeat." +
            s" Not heard since ${current - lastHeartbeat}ms" +
            s" Current: $current, Last heartbeat: $lastHeartbeat")
        }
        missedHeartbeat
    }.toMap.keySet
  }
}
