package com.jalebi.yarn

import com.jalebi.common.{ArgumentUtils, Logging}
import com.jalebi.yarn.CommandConstants.AppMaster

case class ApplicationClientArgs(args: Map[String, String]) {

  //Since precondition check for mandatory arguments is already done, just return the arg
  def getClientClass: String = args(AppMaster.clientClass)

  def getJalebiHome: String = args(AppMaster.jalebiHome)

  def getJarPath: String = args(AppMaster.jarPath)
}

object ApplicationClientArgs extends Logging {

  @throws[IllegalArgumentException]
  def apply(args: Array[String]): ApplicationClientArgs = {
    val usage =
      s"""Usage: scala com.jalebi.yarn.ApplicationClient
         |[--${AppMaster.clientClass} <clientclass>]
         |[--${AppMaster.jalebiHome} <jalebihome>]
         |[--${AppMaster.jarPath} <jarpath>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to ApplicationClient are empty. $usage")
      throw new IllegalArgumentException("Arguments to ApplicationClient are empty.")
    }

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--clientclass" :: value :: tail =>
          nextOption(map ++ Map(AppMaster.clientClass -> value.toString), tail)
        case "--jalebihome" :: value :: tail =>
          nextOption(map ++ Map(AppMaster.jalebiHome -> value.toString), tail)
        case "--jarpath" :: value :: tail =>
          nextOption(map ++ Map(AppMaster.jarPath -> value.toString), tail)
        case option :: _ => LOGGER.warn(s"Unknown option $option.")
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), args.toList)
    ArgumentUtils.validateArgPresent(Seq(AppMaster.clientClass, AppMaster.jarPath, AppMaster.jalebiHome), options, usage)
    ApplicationClientArgs(options)
  }
}
