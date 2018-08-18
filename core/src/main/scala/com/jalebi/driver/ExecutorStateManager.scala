package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement.DatasetState.NONE
import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement.{DatasetState, ExecutorState, TaskType}
import com.jalebi.utils.Logging

import scala.collection.mutable

case class ExecutorStateManager(conf: JalebiConfig) extends Logging {

  private var initializationState = false

  private var currentAction: Option[TaskType] = None

  private val executorIds = mutable.HashSet[String]()

  private val executorIdToParts = mutable.HashMap[String, mutable.Set[String]]()
    .withDefaultValue(mutable.Set.empty)

  private val executorIdToState = mutable.HashMap[String, ExecutorState]()
    .withDefaultValue(NEW)

  private val executorIdToDatasetState = mutable.HashMap[String, DatasetState]()
    .withDefaultValue(NONE)

  private val executorIdToLastHeartbeat = mutable.HashMap[String, Long]()

  def initialize(): Unit = {
    initializationState = true
  }

  def isInitialized: Boolean = initializationState

  def assignPartsToExecutor(executorId: String, parts: Set[String]): Unit = {
    val existingParts = executorIdToParts(executorId)
    executorIdToParts += (executorId -> existingParts.diff(parts))
  }

  def removePartsFromExecutor(executorId: String, parts: Set[String]): Unit = {
    val existingParts = executorIdToParts(executorId)
    executorIdToParts += (executorId -> existingParts.union(parts))
  }

  def markRegistered(executorId: String): Unit = {
    if (executorIdToState.get(executorId).isDefined) {
      executorIdToState(executorId) = REGISTERED
    }
  }

  def markUnregistered(executorId: String): Unit = {
    if (executorIdToState.get(executorId).isDefined) {
      executorIdToState(executorId) = UNREGISTERED
    }
  }

  def updateLastHeartbeat(executorId: String, executorState: ExecutorState, datasetState: DatasetState, heartbeat: Long): Unit = {
    executorIdToState(executorId) = executorState
    executorIdToDatasetState(executorId) = datasetState
    executorIdToLastHeartbeat(executorId) = heartbeat
  }

  def addExecutorId(executorId: String): ExecutorStateManager = {
    executorIds.add(executorId)
    executorIdToState(executorId) = NEW
    executorIdToDatasetState(executorId) = NONE
    this
  }

  def removeExecutorId(executorId: String): ExecutorStateManager = {
    executorIds.remove(executorId)
    executorIdToState.remove(executorId)
    executorIdToDatasetState.remove(executorId)
    this
  }

  def listExecutorIds(): Set[String] = executorIds.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = {
    executorIdToParts.getOrElse(executorId, Set.empty).toSet
  }

  def clearState(): Unit = {
    executorIdToParts.mapValues(mutable.Set.empty)
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
