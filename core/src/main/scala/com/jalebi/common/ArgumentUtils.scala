package com.jalebi.common

object ArgumentUtils extends Logging {

  @throws[IllegalArgumentException]
  def validateArgPresent(keys: Seq[String], options: Map[String, String], usage: String): Unit = {
    keys.foreach(
      key => {
        if (!options.contains(key)) {
          LOGGER.error(s"$key is not provided in arguments. $usage.")
          throw new IllegalArgumentException(s"$key not provided in arguments.")
        }
      }
    )
  }
}
