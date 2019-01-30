package com.jalebi.executor

import com.jalebi.common.{ArgumentUtils, Logging}
import com.jalebi.hdfs.HostPort
import com.jalebi.yarn.CommandConstants._

case class ExecutorArgs(args: Map[String, String]) {

  def getDriverHostPort = HostPort("http", args(ExecutorConstants.driverHost), args(ExecutorConstants.driverPort).toInt)

  def getExecutorId: String = args(ExecutorConstants.executorId)

  def getApplicationId: String = args(AppMaster.applicationId)
}

object ExecutorArgs extends Logging {

  @throws[IllegalArgumentException]
  def apply(args: Array[String]): ExecutorArgs = {
    val usage =
      s"""Usage: com.jalebi.yarn.executor.Executor
         |[--${ExecutorConstants.driverHost} <driverhost>]
         |[--${ExecutorConstants.driverPort} <driverport>]
         |[--${ExecutorConstants.executorId} <executorid>]
         |[--${AppMaster.applicationId} <applicationid>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to Executor are empty. $usage")
      throw new IllegalArgumentException("Arguments to Executor are empty.")
    }

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--applicationid" :: value :: tail =>
          nextOption(map ++ Map(AppMaster.applicationId -> value.toString), tail)
        case "--driverport" :: value :: tail =>
          nextOption(map ++ Map(ExecutorConstants.driverPort -> value.toString), tail)
        case "--driverhost" :: value :: tail =>
          nextOption(map ++ Map(ExecutorConstants.driverHost -> value.toString), tail)
        case "--executorid" :: value :: tail =>
          nextOption(map ++ Map(ExecutorConstants.executorId -> value.toString), tail)
        case option :: _ => LOGGER.warn("Unknown executor option " + option)
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), args.toList)
    ArgumentUtils.validateArgPresent(Seq(ExecutorConstants.driverHost, ExecutorConstants.driverPort, ExecutorConstants.executorId, AppMaster.applicationId), options, usage)
    ExecutorArgs(options)
  }
}
