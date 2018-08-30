package com.jalebi.yarn

import com.jalebi.common.Logging

case class ApplicationMasterArgs(args: Map[String, String]) {

  //Since precondition check for mandatory arguments is already done, just return the arg
  def getApplicationId: String = {
    args(CommandConstants.AppMaster.applicationId)
  }

  def getJarPath: String = {
    args(CommandConstants.AppMaster.jarPath)
  }
}

object ApplicationMasterArgs extends Logging {

  @throws[IllegalArgumentException]
  def apply(args: Array[String]): ApplicationMasterArgs = {
    val usage =
      s"""Usage: scala com.jalebi.yarn.ApplicationMaster
         |[--${CommandConstants.AppMaster.applicationId} <applicationId>]
         |[--${CommandConstants.AppMaster.jarPath} <jarPath>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to ApplicationMaster are empty. $usage")
      throw new IllegalArgumentException("Arguments to ApplicationMaster are empty")
    }

    val arglist = args.toList

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--applicationId" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.AppMaster.applicationId -> value.toString), tail)
        case "--jarPath" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.AppMaster.jarPath -> value.toString), tail)
        case option :: tail => LOGGER.warn("Unknown option " + option)
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), arglist)
    if (options.get(CommandConstants.AppMaster.applicationId).isEmpty) {
      LOGGER.error(s"ApplicationId is not provided in arguments. Usage: $usage.")
      throw new IllegalArgumentException("ApplicationId not provided in arguments.")
    }
    if (options.get(CommandConstants.AppMaster.jarPath).isEmpty) {
      LOGGER.error(s"jarPath is not provided in arguments. Usage: $usage.")
      throw new IllegalArgumentException("jarPath not provided in arguments.")
    }
    ApplicationMasterArgs(options)
  }
}
