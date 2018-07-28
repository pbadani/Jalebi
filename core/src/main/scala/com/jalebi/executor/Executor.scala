package com.jalebi.executor

import java.util.concurrent.atomic.AtomicLong

object Executor {

  private val executorIDCounter = new AtomicLong(0)

  val newExecutorID = s"${ExecutorCommandConstants.executorPrefix}_${executorIDCounter.getAndIncrement()}"

  def main(args: Array[String]): Unit = {
    println("started Executor")
  }
}
