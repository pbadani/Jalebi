package com.jalebi.executor

import akka.actor.{Actor, Props}
import com.jalebi.api.Jalebi
import com.jalebi.common.Logging
import com.jalebi.message.{FindNode, FullResult}

case class JobRunner(executorId: String, jobId: String, jalebi: Jalebi) extends Actor with Logging {
  override def receive: Receive = {
    case FindNode(nodeId, _) =>
      LOGGER.info(s"Finding node $nodeId in $executorId.")
      val result = jalebi.searchNode(nodeId).map(Set(_)).getOrElse(Set.empty)
      LOGGER.info(s"Found ${result.map(_.id).mkString(",")} on $executorId.")
      sender() ! FullResult(executorId, jobId, result)
  }
}

object JobRunner {
  def props(executorId: String, jobId: String, jalebi: Jalebi) = Props(JobRunner(executorId, jobId, jalebi))

  def name(executorId: String, jobId: String) = s"JobRunner${executorId}_$jobId"
}
