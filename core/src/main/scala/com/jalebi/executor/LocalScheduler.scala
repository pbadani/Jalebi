package com.jalebi.executor

import akka.actor.{ActorRef, Props}
import com.jalebi.common.Logging
import com.jalebi.driver.Scheduler
import com.jalebi.hdfs.HostPort
import com.jalebi.message._

import scala.collection.mutable

case class LocalScheduler(applicationId: String, stateMonitorRef: ActorRef, driverHostPort: HostPort) extends Scheduler with Logging {

  private val refs = mutable.HashMap[String, ActorRef]()

  override def receive: Receive = {
    case StartExecutors(executorIds) =>
      executorIds.foreach(executorId => {
        if (refs.contains(executorId)) {
          throw new IllegalStateException(s"Executor $executorId already started.")
        }
        val executorRef = Executor.master.actorOf(Executor.props(executorId, driverHostPort), Executor.name(executorId))
        refs += (executorId -> executorRef)
        LOGGER.info(s"Starting executor $executorId.")
      })
    case StopExecutors =>
      refs.foreach {
        case (_, ref) => ref ! ShutExecutors
      }
  }
}

object LocalScheduler {
  def props(applicationId: String, stateMonitorRef: ActorRef, driverHostPort: HostPort) = Props(LocalScheduler(applicationId, stateMonitorRef, driverHostPort))

  def name() = "LocalScheduler"
}
