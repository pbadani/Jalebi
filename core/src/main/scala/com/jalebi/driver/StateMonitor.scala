package com.jalebi.driver

import akka.actor.{Actor, Props}
import com.jalebi.common.Logging
import com.jalebi.context.JalebiContext
import com.jalebi.message._

case class StateMonitor(executorStateManage: ExecutorStateManage, jContext: JalebiContext) extends Actor with Logging {

  override def receive: Receive = {
    case RegisterExecutor(executorId) =>
      executorStateManage.markRegistered(executorId)
      sender() ! RegistrationAcknowledged(jContext.conf.hdfsHostPort.get)
    case ContainerRequested(executorId, requestId) =>
      LOGGER.info(s"Container requested $executorId, $requestId.")
      executorStateManage.markRequested(executorId, requestId)
    case ContainerAllocated(container) =>
      LOGGER.info(s"Container allocated for request ${container.getAllocationRequestId}.")
      val executorId = executorStateManage.markAllocated(container)
      executorId.foreach { id =>
        sender() ! LaunchContainer(id, container)
      }
    case LoadedDataset(executorId) =>
      LOGGER.info(s"Loaded dataset at $executorId")
      executorStateManage.markLoaded(executorId)
    case Heartbeat(executorId) =>
      LOGGER.info(s"Received $executorId heartbeat.")
      executorStateManage.consumeNextJob(executorId).foreach(sender() ! _)
    case FullResult(executorId, jobId, nodes) =>
      executorStateManage.saveResult(jobId, executorId, nodes.toSeq)
  }
}

object StateMonitor {
  def props(executorStateManage: ExecutorStateManage, jContext: JalebiContext) = Props(StateMonitor(executorStateManage, jContext))

  def name() = "Monitor"
}