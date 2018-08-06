package com.jalebi.yarn

import com.jalebi.context.JalebiContext
import com.jalebi.driver.Scheduler
import org.apache.hadoop.fs.BlockLocation

case class YarnScheduler(context: JalebiContext) extends Scheduler(context) {

  def startExecutors(blockLocations: Map[String, BlockLocation]): Map[String, BlockLocation] = {
    null
  }

}

object YarnScheduler {
  def apply(context: JalebiContext): YarnScheduler = new YarnScheduler(context)
}
