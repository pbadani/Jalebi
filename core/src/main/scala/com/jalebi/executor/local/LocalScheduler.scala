package com.jalebi.executor.local

import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler
import com.jalebi.executor.TaskManager
import com.jalebi.utils.Logging

import scala.collection.mutable

case class LocalScheduler(context: JalebiContext) extends Scheduler(context) with Logging {

  private val threads: mutable.Map[String, Thread] = mutable.HashMap()

  override def startExecutors(executorIds: Set[String]): Unit = {
    executorIds.foreach(executorId => {
      val thread = new Thread(LocalRunnable(TaskManager(executorId), context.driverHostPort), executorId)
      threads += (executorId -> thread)
      LOGGER.info(s"Starting thread for $executorId.")
      thread.start()
    })
  }

  override def shutExecutors(executorIds: Set[String]): Unit = {
    executorIds.foreach(executorId => {
      threads.get(executorId).foreach(t => {
        LOGGER.info(s"Stopping thread for $executorId.")
        t.interrupt()
      })
    })
  }

  override def shutAllExecutors(): Unit = {
    threads.foreach {
      case (executorId, thread) =>
        LOGGER.info(s"Stopping thread for $executorId.")
        thread.interrupt()
    }
  }
}

object LocalScheduler {
  def apply(context: JalebiContext): LocalScheduler = new LocalScheduler(context)
}
