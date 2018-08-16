package com.jalebi.job

import com.jalebi.context.JalebiContext
import com.jalebi.driver.{DriverCoordinatorService, ExecutorState}
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.executor.standalone.LocalScheduler
import com.jalebi.hdfs.{HDFSClient, HostPort}
import com.jalebi.partitioner.HashPartitioner
import com.jalebi.utils.Logging
import com.jalebi.yarn.YarnScheduler

import scala.collection.mutable

case class JobManager(context: JalebiContext) extends Logging {

  private val numOfExecutors = context.conf.options.getNumberOfExecutors().toInt
  lazy private val scheduler = if (context.onLocalMaster) LocalScheduler(context) else YarnScheduler(context)
  lazy private val executorState: ExecutorState = {
    (0 until numOfExecutors).
      foldLeft(ExecutorState(mutable.Set.empty))((acc, _) => acc.addExecutorId(context.newExecutorId()))
  }

  def initialize(numberOfExecutors: Int): Unit = {
    DriverCoordinatorService(this).start()
  }

  def ensureInitialized(): Unit = synchronized {
    if (executorIds.isEmpty) {
      LOGGER.info(s"Starting executors: [${executorIds.mkString(", ")}]")
      scheduler.startExecutors(executorIdToParts.get.listExecutorIds())
    }
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Boolean = {
    ensureInitialized()
    val parts = hdfsClient.listDatasetParts(name)
    executorIdToParts = Some(HashPartitioner.partition(parts, executorIds.get))

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
