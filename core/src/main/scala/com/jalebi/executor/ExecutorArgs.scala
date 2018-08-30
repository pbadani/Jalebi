package com.jalebi.executor

import com.jalebi.common.Logging
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.proto.jobmanagement.HostPort
import com.jalebi.yarn.CommandConstants

case class ExecutorArgs(args: Map[String, String]) {

  def getDriverHostPort: RichHostPort = {
    RichHostPort(HostPort("http", args(CommandConstants.Executor.driverHost), args(CommandConstants.Executor.driverPort).toInt))
  }

  def getExecutorId: String = {
    args(CommandConstants.Executor.executorId)
  }

  def getApplicationId: String = {
    args(CommandConstants.AppMaster.applicationId)
  }
}

object ExecutorArgs extends Logging {

  @throws[IllegalArgumentException]
  def apply(args: Array[String]): ExecutorArgs = {
    val usage =
      s"""Usage: com.jalebi.yarn.executor.Executor
         |[--${CommandConstants.Executor.driverHost} <driverhost>]
         |[--${CommandConstants.Executor.driverPort} <driverport>]
         |[--${CommandConstants.Executor.executorId} <executorid>]
         |[--${CommandConstants.AppMaster.applicationId} <applicationid>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to Executor are empty. $usage")
      throw new IllegalArgumentException("Arguments to Executor are empty")
    }

    val arglist = args.toList

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--applicationid" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.AppMaster.applicationId -> value.toString), tail)
        case "--driverport" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.Executor.driverPort -> value.toString), tail)
        case "--driverhost" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.Executor.driverHost -> value.toString), tail)
        case "--executorid" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.Executor.executorId -> value.toString), tail)
        case option :: _ => LOGGER.warn("Unknown executor option " + option)
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), arglist)
    if (!options.contains(CommandConstants.Executor.driverHost)) {
      LOGGER.error(s"driverhost is not provided in arguments. Usage: $usage")
      throw new IllegalArgumentException("driverhost not provided in arguments.")
    }
    if (!options.contains(CommandConstants.Executor.driverPort)) {
      LOGGER.error(s"driverport is not provided in arguments. Usage: $usage")
      throw new IllegalArgumentException("driverport not provided in arguments.")
    }
    if (!options.contains(CommandConstants.Executor.executorId)) {
      LOGGER.error(s"executorid is not provided in arguments. Usage: $usage")
      throw new IllegalArgumentException("executorid not provided in arguments.")
    }
    if (!options.contains(CommandConstants.AppMaster.applicationId)) {
      LOGGER.error(s"applicationid is not provided in arguments. Usage: $usage")
      throw new IllegalArgumentException("applicationid not provided in arguments.")
    }
    ExecutorArgs(options)
  }
}
