package com.jalebi.driver

import scala.collection.mutable

object E extends Enumeration {
  type State = Value
  val NEW, REGISTERED, RUNNING_JOB, IDLE, UNREGISTERED, ASSUMED_DEAD = Value
}

case class ExecutorState() {

  private var isInitialized = false

  private val executorIds = mutable.HashSet[String]()

  private val executorIdToParts = mutable.HashMap[String, mutable.Set[String]]()
    .withDefaultValue(mutable.Set.empty)

  private val executorIdToState = mutable.HashMap[String, E.State]()
    .withDefaultValue(E.NEW)

  def initalize(): Unit = {
    isInitialized = true
  }

  def isInitalized: Boolean = isInitialized

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
      executorIdToState(executorId) = E.REGISTERED
    }
  }

  def addExecutorId(executorId: String): ExecutorState = {
    executorIds.add(executorId)
    executorIdToState(executorId) = E.NEW
    this
  }

  def removeExecutorId(executorId: String): ExecutorState = {
    executorIds.remove(executorId)
    this
  }

  def listExecutorIds(): Set[String] = executorIds.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = executorIdToParts.getOrElse(executorId, Set.empty).toSet

}
