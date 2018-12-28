package com.jalebi.yarn

import com.jalebi.common.{ArgumentUtils, Logging}
import com.jalebi.yarn.CommandConstants.AppMaster

case class ApplicationMasterArgs(args: Map[String, String]) {

  //Since precondition check for mandatory arguments is already done, just return the arg
  def getApplicationId: String = {
    args(AppMaster.applicationId)
  }

  def getJarPath: String = {
    args(AppMaster.jarPath)
  }
}

object ApplicationMasterArgs extends Logging {

  @throws[IllegalArgumentException]
  def createArgsFromEnvironment(): ApplicationMasterArgs = {
    val m = Seq(AppMaster.applicationId, AppMaster.jarPath).map(key => {
      val value = System.getenv().get(key)
      if (value == null) {
        throw new IllegalArgumentException(s"Environment entry not defined for key $key.")
      }
      (key, value)
    }).toMap
    ApplicationMasterArgs(m)
  }

  @throws[IllegalArgumentException]
  def apply(args: Array[String]): ApplicationMasterArgs = {
    val usage =
      s"""Usage: scala com.jalebi.yarn.ApplicationMaster
         |[--${AppMaster.applicationId} <applicationid>]
         |[--${AppMaster.jarPath} <jarpath>]
         |""".stripMargin

    if (args.isEmpty) {
      LOGGER.error(s"Arguments to ApplicationMaster are empty. $usage")
      throw new IllegalArgumentException("Arguments to ApplicationMaster are empty")
    }

    def nextOption(map: Map[String, String], list: List[String]): Map[String, String] = {
      list match {
        case Nil => map
        case "--applicationid" :: value :: tail =>
          nextOption(map ++ Map(AppMaster.applicationId -> value.toString), tail)
        case "--jarpath" :: value :: tail =>
          nextOption(map ++ Map(AppMaster.jarPath -> value.toString), tail)
        case option :: _ => LOGGER.warn(s"Unknown option $option.")
          //          System.exit(1)
          Map.empty
      }
    }

    val options = nextOption(Map(), args.toList)
    ArgumentUtils.validateArgPresent(Seq(AppMaster.applicationId, AppMaster.jarPath), options, usage)
    ApplicationMasterArgs(options)
  }
}
