package com.jalebi.job

import com.jalebi.context.JalebiContext
import com.jalebi.driver.{DriverCoordinatorService, Scheduler}
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.hdfs.{HDFSClient, HostPort}
import com.jalebi.executor.standalone.LocalScheduler
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler
import org.apache.hadoop.fs.BlockLocation

import scala.collection.mutable

case class JobManager(context: JalebiContext) extends Logging {

  var executorIdToBlockLocations: Map[String, BlockLocation] = _
  val registeredExecutors: mutable.Set[String] = new mutable.HashSet[String]()

  lazy private val scheduler: Scheduler = {
    if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Boolean = {
    val blockLocations: Array[BlockLocation] = hdfsClient.getFileBlockLocations(name)

    val service = new Thread(DriverCoordinatorService(this))
    service.start()
    executorIdToBlockLocations = assignExecutorIds(blockLocations)
    LOGGER.info(s"Starting executors: [${registeredExecutors.mkString(", ")}]")
    scheduler.startExecutors(executorIdToBlockLocations)
    false
  }

  private def assignExecutorIds(blockLocations: Array[BlockLocation]): Map[String, BlockLocation] = {
    blockLocations.map(blockLocation => {
      (context.newExecutorId(), blockLocation)
    }).toMap[String, BlockLocation]
  }

  def shutRunningExecutors(): Unit = {
    LOGGER.info(s"Shutting down executors: [${registeredExecutors.mkString(", ")}]")
    scheduler.shutExecutors(registeredExecutors)
  }

  def driverHostPort: HostPort = context.driverHostPort
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
