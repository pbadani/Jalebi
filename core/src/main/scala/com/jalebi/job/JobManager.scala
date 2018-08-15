package com.jalebi.job

import com.jalebi.context.JalebiContext
import com.jalebi.driver.DriverCoordinatorService
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.ExecutorIdToParts
import com.jalebi.executor.standalone.LocalScheduler
import com.jalebi.hdfs.{HDFSClient, HostPort}
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler

case class JobManager(context: JalebiContext) extends Logging {

  private var executorIdToParts: Option[ExecutorIdToParts] = None

  lazy private val scheduler = if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String, numberOfExecutors: Int): Boolean = {
    DriverCoordinatorService(this).start()
    val parts = hdfsClient.listDatasetParts(name)
    val executorIds = (0 until numberOfExecutors).map(_ => context.newExecutorId()).toSet
    executorIdToParts = Some(HashPartitioner.partition(parts, executorIds))
    LOGGER.info(s"Starting executors: [${executorIds.mkString(", ")}]")
    scheduler.startExecutors(executorIdToParts.get.listExecutorIds())

    true
  }

  def shutRunningExecutors(): Unit = {
    LOGGER.info(s"Shutting All executors.")
    scheduler.shutAllExecutors()
  }

  def driverHostPort: HostPort = context.driverHostPort
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
