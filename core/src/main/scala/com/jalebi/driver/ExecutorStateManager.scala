package com.jalebi.driver

import com.jalebi.proto.jobmanagement.ExecutorState
import com.jalebi.proto.jobmanagement.ExecutorState.{NEW, REGISTERED, UNREGISTERED}

import scala.collection.mutable

case class ExecutorStateManager() {

  private var initializationState = false

  private val executorIds = mutable.HashSet[String]()

  private val executorIdToParts = mutable.HashMap[String, mutable.Set[String]]()
    .withDefaultValue(mutable.Set.empty)

  private val executorIdToState = mutable.HashMap[String, ExecutorState]()
    .withDefaultValue(NEW)

  def initialize(): Unit = {
    initializationState = true
  }

  def isInitialized: Boolean = initializationState

  def assignPartsToExecutor(executorId: String, parts: mutable.Set[String]): Unit = {
    val existingParts = executorIdToParts(executorId)
    executorIdToParts += (executorId -> existingParts.diff(parts))
  }

  def removePartsFromExecutor(executorId: String, parts: mutable.Set[String]): Unit = {
    val existingParts = executorIdToParts(executorId)
    executorIdToParts += (executorId -> parts.union(existingParts))
  }

  def markRegistered(executorId: String): Unit = {
    val state = executorIdToState.get(executorId)
    if (state.isDefined) {
      executorIdToState(executorId) = REGISTERED
    }
  }

  def markUnregistered(executorId: String): Unit = {
    val state = executorIdToState.get(executorId)
    if (state.isDefined) {
      executorIdToState(executorId) = UNREGISTERED
    }
  }

  def updateLastHeartbeat(executorId: String): Unit = {
    val state = executorIdToState.get(executorId)
    //    if (state.isDefined) {
    //      executorIdToState(executorId) = E.UNREGISTERED
    //    }
  }

  def addExecutorId(executorId: String): ExecutorStateManager = {
    executorIds.add(executorId)
    executorIdToState(executorId) = NEW
    this
  }

  def removeExecutorId(executorId: String): ExecutorStateManager = {
    executorIds.remove(executorId)
    this
  }

  def listExecutorIds(): Set[String] = executorIds.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = executorIdToParts.getOrElse(executorId, Set.empty).toSet

}
