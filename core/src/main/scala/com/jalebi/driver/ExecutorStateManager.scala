package com.jalebi.driver

import com.jalebi.common.Logging
import com.jalebi.context.JalebiConfig
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.proto.jobmanagement.DatasetState.NONE
import com.jalebi.proto.jobmanagement.ExecutorState._
import com.jalebi.proto.jobmanagement.{DatasetState, ExecutorState, TaskRequest}
import org.apache.hadoop.yarn.api.records.Container

import scala.collection.mutable

case class ExecutorStateManager(conf: JalebiConfig) extends Logging {

  case class State(parts: Set[String],
                   executorState: ExecutorState,
                   datasetState: DatasetState,
                   container: Option[Container],
                   nextAction: Option[TaskRequest])

  private val default = State(parts = Set.empty, executorState = NEW, datasetState = NONE, container = None, nextAction = None)

  private var initializationState = false

  private val executorIdToState = mutable.HashMap[String, State]().withDefaultValue(default)

  private val executorIdToLastHeartbeat = mutable.HashMap[String, Long]()

  def initialize(): Unit = {
    waitForAllExecutorsToBeRegistered()
    initializationState = true
  }

  def isInitialized: Boolean = initializationState

  def waitForAllExecutorsToBeRegistered(): Unit = {
    while (executorIdToState.exists {
      case (_, state) => state.executorState == NEW
    }) {
      val unregistered = executorIdToState.filter {
        case (_, state) => state.executorState == NEW
      }.map {
        case (executorId, _) => executorId
      }
      LOGGER.info(s"waiting for ${unregistered.mkString(", ")} to be registered.")
      Thread.sleep(1000)
    }
  }

  def loadPartsToExecutors(jobId: String, parts: Set[String], name: String): Unit = {
    clearParts()
    HashPartitioner.partition(parts, listExecutorIds()).foreach {
      case (e, p) => assignPartsToExecutor(jobId, e, p, name)
    }
  }

  def assignNewTask(taskRequest: TaskRequest): Unit = {
    executorIdToState.keySet.foreach(executorId => {
      updateState(executorId, state => {
        state.copy(nextAction = Some(taskRequest.copy(parts = state.parts.toSeq)))
      })
    })
  }

  def consumeNextTask(executorId: String): Option[TaskRequest] = {
    val next = executorIdToState(executorId).nextAction
    next.foreach(_ => updateState(executorId, state => state.copy(nextAction = None)))
    next
  }

  private def assignPartsToExecutor(jobId: String, executorId: String, parts: Set[String], name: String): Unit = {
    LOGGER.info(s"Assigned parts [${parts.mkString(",")}] to executor $executorId.")
    updateState(executorId, state => {
      val request = TaskRequestBuilder.loadDatasetRequest(jobId, name, parts.toSeq)
      state.copy(parts = parts, nextAction = Some(request))
    })
  }

  def removePartsFromExecutor(executorId: String, parts: Set[String]): Unit = {
    LOGGER.info(s"Removed parts ${parts.mkString(",")} from executor $executorId.")
    updateState(executorId, state => state.copy(parts = state.parts -- parts))
  }

  def findExecutorToAssignContainer(container: Container): Option[String] = this.synchronized {
    executorIdToState.collectFirst {
      case (executorId, state) if state.container.isEmpty && state.executorState == NEW =>
        updateState(executorId, state => state.copy(container = Some(container), executorState = REQUESTED))
        executorId
    }
  }

  def markAllocated(executorId: String): Unit = {
    LOGGER.info(s"Allocated $executorId.")
    if (!executorIdToState.contains(executorId)) {
      throw new IllegalStateException(s"Executor $executorId has not been added yet.")
    }
    updateState(executorId, state => state.copy(executorState = ALLOCATED))
  }

  def markRegistered(executorId: String): Unit = {
    LOGGER.info(s"Registering $executorId.")
    if (!executorIdToState.contains(executorId)) {
      throw new IllegalStateException(s"Executor $executorId has not been added yet.")
    }
    updateState(executorId, state => state.copy(executorState = REGISTERED))
  }

  def markUnregistered(executorId: String): Unit = {
    LOGGER.info(s"Unregistering $executorId.")
    if (!executorIdToState.contains(executorId)) {
      throw new IllegalStateException(s"Executor $executorId has not been added yet.")
    }
    updateState(executorId, state => state.copy(executorState = UNREGISTERED))
  }

  def updateLastHeartbeat(executorId: String, eState: ExecutorState, dState: DatasetState, heartbeat: Long): Unit = {
    updateState(executorId, state => state.copy(executorState = eState, datasetState = dState))
    executorIdToLastHeartbeat(executorId) = heartbeat
  }

  def addExecutor(executorId: String): Unit = {
    LOGGER.info(s"Adding executor $executorId.")
    executorIdToState.put(executorId, default)
  }

  def removeExecutor(executorId: String): Unit = {
    LOGGER.info(s"Removing executor $executorId.")
    executorIdToState.remove(executorId)
  }

  def forEachExecutor(f: (String, State) => Unit): Unit = {
    executorIdToState.foreach {
      case (executorId, state) => f(executorId, state)
    }
  }

  private def updateState(executorId: String, oldToNewState: State => State) = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> oldToNewState(state))
  }

  def listExecutorIds(): Set[String] = executorIdToState.keySet.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = {
    executorIdToState(executorId).parts
  }

  def clearParts(): Unit = {
    executorIdToState.keySet.foreach(executorId => {
      updateState(executorId, state => {
        state.copy(parts = Set.empty, datasetState = NONE, nextAction = None)
      })
    })
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
