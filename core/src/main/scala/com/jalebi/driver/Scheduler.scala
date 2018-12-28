package com.jalebi.driver

import akka.actor.Actor
import com.jalebi.context.JalebiContext

abstract class Scheduler(context: JalebiContext) extends Actor {
  def startExecutors(executorIds: Set[String]): Unit

  def shutExecutors(executorIds: Set[String]): Unit

  def shutAllExecutors(): Unit
}
