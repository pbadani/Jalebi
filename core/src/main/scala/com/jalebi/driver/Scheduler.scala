package com.jalebi.driver

import com.jalebi.context.JalebiContext
import org.apache.hadoop.fs.BlockLocation

import scala.collection.mutable

abstract class Scheduler(context: JalebiContext) {
  def startExecutors(executorIdsToBlockLocations: Map[String, BlockLocation]): Map[String, BlockLocation]

  def shutExecutors(executorIds: mutable.Set[String])
}
