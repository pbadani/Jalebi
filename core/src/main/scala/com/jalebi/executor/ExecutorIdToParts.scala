package com.jalebi.executor

import scala.collection.mutable

case class ExecutorIdToParts(mapping: mutable.Map[String, Set[String]]) {

  def listExecutorIds(): Set[String] = mapping.keySet.toSet

  def listPartsForExecutorId(executorId: String): Set[String] = mapping.getOrElse(executorId, Set.empty)

//  def reassignExecutorParts(oldExecutor: String, newExecutor: String): Unit = {
//    val oldExecutorParts = mapping.get(oldExecutor)
//    if (oldExecutorParts.isDefined) {
//      mapping -= oldExecutorParts
//      mapping += (newExecutor -> oldExecutorParts)
//    }
//  }
}
