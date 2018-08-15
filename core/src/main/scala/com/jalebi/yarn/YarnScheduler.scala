package com.jalebi.yarn

import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler

case class YarnScheduler(context: JalebiContext) extends Scheduler(context) {

  override def startExecutors(executorIds: Set[String]): Unit = {

  }

  override def shutExecutors(executorIds: Set[String]): Unit = {

  }

  override def shutAllExecutors(): Unit = {

  }
}

object YarnScheduler {
  def apply(context: JalebiContext): YarnScheduler = new YarnScheduler(context)
}
