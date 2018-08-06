package com.jalebi.standalone

import com.jalebi.hdfs.HostPort

case class LocalRunnable(threadId: String, driverHostPort: HostPort) extends Runnable {

  private var running = true

  override def run(): Unit = {

  }

  def terminate(): Unit = {
    running = false
  }
}
