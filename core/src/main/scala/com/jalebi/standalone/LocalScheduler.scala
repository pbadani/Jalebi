package com.jalebi.standalone

import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler
import com.jalebi.utils.Logging
import org.apache.hadoop.fs.BlockLocation

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class LocalScheduler(context: JalebiContext) extends Scheduler(context) with Logging {

  private val threads: ListBuffer[Thread] = new ListBuffer[Thread]()

  override def startExecutors(blockLocations: Map[String, BlockLocation]): Unit = {
    val driverHostPort = context.driverHostPort
    blockLocations.foreach {
      case (executorId, BlockLocation) =>
        val runnable = LocalRunnable(executorId, driverHostPort)
        val thread = new Thread(runnable, executorId)
        threads += thread
        LOGGER.info(s"Starting thread for id $executorId")
        thread.start()
    }
  }

  override def shutExecutors(executorIds: mutable.Set[String]): Unit = {
    if (threads.isEmpty) {
      LOGGER.warn(s"No threads to stop.")
    } else {
      threads.foreach(thread => {
        LOGGER.info(s"Stopping thread for id ${thread.getId}")
        thread.interrupt()
      })
    }
  }
}

object LocalScheduler {
  def apply(context: JalebiContext): LocalScheduler = new LocalScheduler(context)
}
