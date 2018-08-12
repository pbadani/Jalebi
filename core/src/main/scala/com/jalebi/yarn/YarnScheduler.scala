package com.jalebi.yarn

import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler
import org.apache.hadoop.fs.BlockLocation

import scala.collection.mutable

case class YarnScheduler(context: JalebiContext) extends Scheduler(context) {

  override def startExecutors(blockLocations: Map[String, BlockLocation]) = {

  }

  override def shutExecutors(executorIds: mutable.Set[String]): Unit = {

  }
}

object YarnScheduler {
  def apply(context: JalebiContext): YarnScheduler = new YarnScheduler(context)
}
