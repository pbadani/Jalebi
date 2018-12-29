package com.jalebi.driver

import com.jalebi.common.Logging
import com.jalebi.context.JalebiContext
import com.jalebi.message.{ExecutorAction, LoadDatasetTask}
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.proto.jobmanagement.{DatasetState, TaskRequest}
import org.apache.hadoop.yarn.api.records.Container

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

sealed trait JobManagerData extends Logging

object EmptyExecutorStateManager extends JobManagerData

case class ExecutorStateManage(jContext: JalebiContext) extends JobManagerData with Logging {

  private val executorIdToState = new mutable.HashMap[String, StateValue]()
  private var waitToRegister: Option[Promise[ExecutorState]] = None
  private var waitToLoad: Option[Promise[ExecutorState]] = None
  private var waitToUnregister: Option[Promise[ExecutorState]] = None

  //This is a blocking call made by the JobManager because we want to wait for all the executors
  //to be registered before we start executing the jobs.
  def waitForAllToRegister(duration: Duration): Unit = {
    val p = Promise[ExecutorState]()
    waitToRegister = Some(p)
    Await.ready(p.future, duration)
  }

  def waitForAllToLoad(duration: Duration): Unit = {
    val p = Promise[ExecutorState]()
    waitToLoad = Some(p)
    Await.ready(p.future, duration)
  }

  def waitForAllToUnregister(duration: Duration): Unit = {
    val p = Promise[ExecutorState]()
    waitToUnregister = Some(p)
    Await.ready(p.future, duration)
  }

  def loadPartsToExecutors(jobId: String, allParts: Set[String], name: String): Unit = {
    clearParts()
    HashPartitioner.partition(allParts, listExecutorIds()).foreach {
      case (executorId, parts) =>
        LOGGER.info(s"Assigned parts [${parts.mkString(",")}] to $executorId.")
        updateState(executorId, state => {
          state.copy(parts = parts, nextAction = Some(LoadDatasetTask(jobId, name, parts)))
        })
    }
  }

  def assignNewTask(executorAction: ExecutorAction): Unit = {
    executorIdToState.keySet.foreach(executorId => {
      updateState(executorId, state => {
        state.copy(nextAction = Some(executorAction))
      })
    })
  }

  def consumeNextTask(executorId: String): Option[ExecutorAction] = {
    val next = executorIdToState(executorId).nextAction
    next.foreach(_ => updateState(executorId, state => state.copy(nextAction = None)))
    next
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
    if (areAll(REGISTERED)) {
      waitToRegister.foreach(_.success(REGISTERED))
    }
  }

  def markLoaded(executorId: String): Unit = {
    if (!executorIdToState.contains(executorId)) {
      throw new IllegalStateException(s"Executor $executorId has not been added yet.")
    }
    updateState(executorId, state => state.copy(executorState = DATASET_LOADED))
    if (areAll(DATASET_LOADED)) {
      waitToLoad.foreach(_.success(DATASET_LOADED))
    }
  }

  def areAll(executorState: ExecutorState): Boolean = executorIdToState.values.forall(_.executorState == executorState)

  def markUnregistered(executorId: String): Unit = {
    LOGGER.info(s"Unregistering $executorId.")
    if (!executorIdToState.contains(executorId)) {
      throw new IllegalStateException(s"Executor $executorId has not been added yet.")
    }
    updateState(executorId, state => state.copy(executorState = UNREGISTERED))
    if (areAll(UNREGISTERED)) {
      waitToLoad.foreach(_.success(UNREGISTERED))
    }
  }

  def addExecutor(executorId: String, stateValue: StateValue): Unit = {
    LOGGER.info(s"Adding executor $executorId.")
    executorIdToState.put(executorId, stateValue)
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

object ExecutorStateManage {
  val default = StateValue(parts = Set.empty, executorState = NEW, datasetState = DatasetState.NONE, container = None, nextAction = None)
}