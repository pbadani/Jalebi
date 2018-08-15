package com.jalebi.driver

import com.jalebi.context.JalebiContext

abstract class Scheduler(context: JalebiContext) {
  def startExecutors(executorIds: Set[String]): Unit

  def shutExecutors(executorIds: Set[String]): Unit

  def shutAllExecutors(): Unit
}
