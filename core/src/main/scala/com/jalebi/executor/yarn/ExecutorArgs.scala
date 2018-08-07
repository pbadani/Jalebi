package com.jalebi.executor.yarn

import com.jalebi.utils.Logging
import com.jalebi.yarn.CommandConstants

case class ExecutorArgs(args: Map[String, String]) {

  def getDriverURL: String = {
    args(CommandConstants.Executor.driverURL)
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
         |[--${CommandConstants.Executor.driverURL} <driverURL>]
         |[--${CommandConstants.AppMaster.applicationId} <applicationId>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to Executor are empty. $usage")
      throw new IllegalArgumentException("Arguments to Executor are empty")
    }

    val arglist = args.toList

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--applicationId" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.AppMaster.applicationId -> value.toString), tail)
        case "--driverURL" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.Executor.driverURL -> value.toString), tail)
        case option :: tail => LOGGER.warn("Unknown executor option " + option)
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), arglist)
    if (options.get(CommandConstants.Executor.driverURL).isEmpty) {
      LOGGER.error(s"DriverURL is not provided in arguments. Usage: $usage")
      throw new IllegalArgumentException("DriverURL not provided in arguments˚")
    }
    if (options.get(CommandConstants.AppMaster.applicationId).isEmpty) {
      LOGGER.error(s"ApplicationId is not provided in arguments. Usage: $usage")
      throw new IllegalArgumentException("ApplicationId not provided in arguments˚")
    }
    ExecutorArgs(options)
  }
}
