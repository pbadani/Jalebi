package com.jalebi.executor.standalone

import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler
import com.jalebi.utils.Logging
import org.apache.hadoop.fs.BlockLocation

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class LocalScheduler(context: JalebiContext) extends Scheduler(context) with Logging {

  private val threads: ListBuffer[Thread] = new ListBuffer[Thread]()

  override def startExecutors(parts: Map[String, String]): Unit = {
    parts.foreach {
      case (executorId, _) =>
        val thread = new Thread(LocalRunnable(executorId, context.driverHostPort), executorId)
        threads += thread
        LOGGER.info(s"Starting thread for $executorId.")
        thread.start()
    }
  }

  override def shutExecutors(executorIds: mutable.Set[String]): Unit = {
    if (threads.isEmpty) {
      LOGGER.warn(s"No threads to stop.")
    } else {
      threads.foreach(thread => {
        LOGGER.info(s"Stopping thread for ${thread.getId}.")
        thread.interrupt()
      })
    }
  }
}

object LocalScheduler {
  def apply(context: JalebiContext): LocalScheduler = new LocalScheduler(context)
}
