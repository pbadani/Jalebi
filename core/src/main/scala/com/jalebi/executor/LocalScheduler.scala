package com.jalebi.executor

import akka.actor.{ActorRef, Props}
import com.jalebi.common.Logging
import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler
import com.jalebi.message._

import scala.collection.mutable

case class LocalScheduler(jContext: JalebiContext, applicationId: String) extends Scheduler(jContext) with Logging {

  private val refs = mutable.HashMap[String, ActorRef]()

  override def startExecutors(executorIds: Set[String]): Unit = {

  }

  override def shutExecutors(executorIds: Set[String]): Unit = {
    executorIds.foreach(executorId => {
      refs.get(executorId).foreach(executorRef => context.stop(executorRef))
      LOGGER.info(s"Stopping executor $executorId.")
    })
  }

  override def shutAllExecutors(): Unit = {
    shutExecutors(refs.keySet.toSet)
  }

  override def receive: Receive = {
    case StartExecutors(executorIds, hostPort) =>
      executorIds.foreach(executorId => {
        if (refs.contains(executorId)) {
          throw new IllegalStateException(s"Executor $executorId already started.")
        }
        val executorRef = Executor.master.actorOf(Executor.props(executorId, hostPort), Executor.name(executorId))
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
  def props(jContext: JalebiContext, applicationId: String) = Props(LocalScheduler(jContext, applicationId))

  def name() = "LocalScheduler"
}
