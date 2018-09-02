package com.jalebi.executor

import com.jalebi.common.Logging
import com.jalebi.context.JalebiContext
import com.jalebi.driver.{ExecutorStateManager, Scheduler}

import scala.collection.mutable

case class LocalScheduler(context: JalebiContext, executorStateManager: ExecutorStateManager, applicationId: String) extends Scheduler(context) with Logging {

  private val numOfExecutors = context.conf.getNumberOfExecutors().toInt
  (0 until numOfExecutors).foreach(_ => executorStateManager.addExecutor(context.newExecutorId(applicationId)))
  private val threads: mutable.Map[String, Thread] = mutable.HashMap()

  override def startExecutors(executorIds: Set[String]): Unit = {
    executorIds.foreach(executorId => {
      val thread = new Thread(Executor(TaskManager(executorId), context.driverHostPort), executorId)
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
  def apply(context: JalebiContext, executorStateManager: ExecutorStateManager, applicationId: String): LocalScheduler = new LocalScheduler(context, executorStateManager, applicationId)
}
