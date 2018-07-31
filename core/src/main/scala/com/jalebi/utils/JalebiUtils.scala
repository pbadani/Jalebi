package com.jalebi.utils

import org.apache.hadoop.yarn.api.ApplicationConstants

import scala.collection.mutable.HashMap

object JalebiUtils {

  def addPathToEnvironment(env: HashMap[String, String], key: String, value: String): Unit = {
    val newValue =
      if (env.contains(key)) {
        env(key) + ApplicationConstants.CLASS_PATH_SEPARATOR  + value
      } else {
        value
      }
    env.put(key, newValue)
  }
}
