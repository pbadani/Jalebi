package com.jalebi.context

case class JalebiConfigOptions(o: Map[String, String]) {

  def getPartitionSize(default: String = "100"): String = {
    o.getOrElse(JalebiConfigOptions.PARTITION_SIZE, default)
  }

  def getHeartbeatInterval(default: String = "5"): String = {
    o.getOrElse(JalebiConfigOptions.HEARTBEAT_INTERVAL, default)
  }

  def getHeartbeatMissBuffer(default: String = "2"): String = {
    o.getOrElse(JalebiConfigOptions.HEARTBEAT_MISS_BUFFER, default)
  }

  def getNumberOfExecutors(default: String = "5"): String = {
    o.getOrElse(JalebiConfigOptions.NUM_EXECUTORS, default)
  }
}

object JalebiConfigOptions {
  val NUM_EXECUTORS = "jalebi.conf.num.executors"
  val PARTITION_SIZE = "jalebi.conf.partition.size"
  val HEARTBEAT_INTERVAL = "jalebi.conf.heartbeat.interval"
  val HEARTBEAT_MISS_BUFFER = "jalebi.conf.heartbeat.miss.buffer"
}
