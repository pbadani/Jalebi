package com.jalebi.job

import com.jalebi.context.JalebiContext
import com.jalebi.driver.{DriverCoordinatorService, Scheduler}
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.standalone.LocalScheduler
import com.jalebi.hdfs.{HDFSClient, HostPort}
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler

import scala.collection.mutable

case class JobManager(context: JalebiContext) extends Logging {

  var executorIdToParts: Map[String, String] = _
  val registeredExecutors: mutable.Set[String] = new mutable.HashSet[String]()

  lazy private val scheduler: Scheduler = {
    if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Boolean = {
    val parts = hdfsClient.listDatasetParts(name)
    DriverCoordinatorService(this).start()
    executorIdToParts = assignExecutorIds(parts)
    LOGGER.info(s"Starting executors: [${registeredExecutors.mkString(", ")}]")
    scheduler.startExecutors(executorIdToParts)
    false
  }

  private def assignExecutorIds(parts: Set[String]): Map[String, String] = {
    parts.map(part => (context.newExecutorId(), part)).toMap[String, String]
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
