package com.jalebi.context

case class JalebiConfigOptions(o: Map[String, String]) {

  def getPartitionSize(default: String = "100"): String = {
    o.getOrElse(JalebiConfigOptions.PARTITION_SIZE, default)
  }
}

object JalebiConfigOptions {
  val PARTITION_SIZE = "jalebi.conf.partition.size"
}
