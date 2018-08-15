package com.jalebi.driver

case class ExecutorIdToParts(mapping: Map[String, Set[String]]) {

  def listExecutorIds(): Set[String] = mapping.keySet

  def listPartsForExecutorId(executorId: String): Set[String] = mapping.getOrElse(executorId, Set.empty)

//  def reassignExecutorParts(oldExecutor: String, newExecutor: String): Unit = {
//    val oldExecutorParts = mapping.get(oldExecutor)
//    if (oldExecutorParts.isDefined) {
//      mapping -= oldExecutorParts
//      mapping += (newExecutor -> oldExecutorParts)
//    }
//  }
}
