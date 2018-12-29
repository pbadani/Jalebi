package com.jalebi.driver

import com.jalebi.common.Logging
import com.jalebi.context.JalebiContext
import com.jalebi.message.ExecutorAction
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.proto.jobmanagement.{DatasetState, TaskRequest}
import org.apache.hadoop.yarn.api.records.Container

import scala.collection.mutable

case class ExecutorStateManager(jContext: JalebiContext) extends Logging {

  private val default = StateValue(parts = Set.empty, executorState = NEW, datasetState = DatasetState.NONE, container = None, nextAction = None)

  private var initializationState = false

  private val executorIdToState = mutable.HashMap[String, StateValue]().withDefaultValue(default)

  private val executorIdToLastHeartbeat = mutable.HashMap[String, Long]()

  def initialize1(): Unit = {
    waitForAllExecutorsToBeRegistered()
    initializationState = true
  }

  def isInitialized: Boolean = initializationState

  private[driver] def waitForAllExecutorsToBeRegistered(): Unit = {
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
    LOGGER.info("All executors are registered.")
  }

  def loadPartsToExecutors(jobId: String, parts: Set[String], name: String): Unit = {
    clearParts()
    HashPartitioner.partition(parts, listExecutorIds()).foreach {
      case (e, p) => assignPartsToExecutor(jobId, e, p, name)
    }
  }

  def assignNewTask(taskRequest: TaskRequest): Unit = {
//    executorIdToState.keySet.foreach(executorId => {
//      updateState(executorId, state => {
//        state.copy(nextAction = Some(taskRequest.copy(parts = state.parts.toSeq)))
//      })
//    })
  }

  def consumeNextTask(executorId: String): Option[ExecutorAction] = {
    val next = executorIdToState(executorId).nextAction
    next.foreach(_ => updateState(executorId, state => state.copy(nextAction = None)))
    next
  }

  private def assignPartsToExecutor(jobId: String, executorId: String, parts: Set[String], name: String): Unit = {
//    LOGGER.info(s"Assigned parts [${parts.mkString(",")}] to executor $executorId.")
//    updateState(executorId, state => {
//      val request = TaskRequestBuilder.loadDatasetRequest(jobId, name)
//      state.copy(parts = parts, nextAction = Some(request))
//    })
  }

  private def removePartsFromExecutor(executorId: String, parts: Set[String]): Unit = {
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

  def forEachExecutor(f: (String, StateValue) => Unit): Unit = {
    executorIdToState.foreach {
      case (executorId, state) => f(executorId, state)
    }
  }

  private def updateState(executorId: String, mapState: StateValue => StateValue) = {
    val state = executorIdToState(executorId)
    executorIdToState += (executorId -> mapState(state))
  }

  def listExecutorIds(): Set[String] = executorIdToState.keySet.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = {
    executorIdToState(executorId).parts
  }

  def clearParts(): Unit = {
    executorIdToState.keySet.foreach(executorId => {
      updateState(executorId, state => {
        state.copy(parts = Set.empty, datasetState = DatasetState.NONE, nextAction = None)
      })
    })
  }
}


object ExecutorStateManager {
  val default = StateValue(parts = Set.empty, executorState = NEW, datasetState = DatasetState.NONE, container = None, nextAction = None)
}