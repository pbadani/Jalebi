package com.jalebi.executor.yarn

import com.jalebi.utils.Logging

object Executor extends Logging {

  def main(args: Array[String]): Unit = {
    LOGGER.info("####################################")
    LOGGER.info("started Executor")
    Thread.sleep(1000000)
  }
}
