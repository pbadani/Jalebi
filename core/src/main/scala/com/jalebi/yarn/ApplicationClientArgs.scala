package com.jalebi.yarn

import com.jalebi.common.Logging

case class ApplicationClientArgs(args: Map[String, String]) {

  //Since precondition check for mandatory arguments is already done, just return the arg
  def getClientClass: String = args(CommandConstants.AppMaster.clientClass)

  def getJarPath: String = args(CommandConstants.AppMaster.jarPath)
}

object ApplicationClientArgs extends Logging {

  @throws[IllegalArgumentException]
  def apply(args: Array[String]): ApplicationClientArgs = {
    val usage =
      s"""Usage: scala com.jalebi.yarn.ApplicationClient
         |[--${CommandConstants.AppMaster.clientClass} <clientclass>]
         |[--${CommandConstants.AppMaster.jarPath} <jarpath>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to ApplicationMaster are empty. $usage")
      throw new IllegalArgumentException("Arguments to ApplicationMaster are empty")
    }

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--clientclass" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.AppMaster.clientClass -> value.toString), tail)
        case "--jarpath" :: value :: tail =>
          nextOption(map ++ Map(CommandConstants.AppMaster.jarPath -> value.toString), tail)
        case option :: tail => LOGGER.warn("Unknown option " + option)
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), args.toList)
    if (options.get(CommandConstants.AppMaster.clientClass).isEmpty) {
      LOGGER.error(s"clientclass is not provided in arguments. Usage: $usage.")
      throw new IllegalArgumentException("clientclass not provided in arguments.")
    }
    if (options.get(CommandConstants.AppMaster.jarPath).isEmpty) {
      LOGGER.error(s"jarpath is not provided in arguments. Usage: $usage.")
      throw new IllegalArgumentException("jarpath not provided in arguments.")
    }
    ApplicationClientArgs(options)
  }
}
