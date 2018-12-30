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
    case LoadedDataset(executorId) =>
      LOGGER.info(s"Loaded dataset at $executorId")
      executorStateManage.markLoaded(executorId)
    case Heartbeat(executorId) =>
      LOGGER.info(s"Received $executorId heartbeat.")
      executorStateManage.consumeNextTask(executorId).foreach {
        sender() ! _
      }
  }
}

object StateMonitor {
  def props(executorStateManage: ExecutorStateManage, jContext: JalebiContext) = Props(StateMonitor(executorStateManage, jContext))

  def name() = "Monitor"
}