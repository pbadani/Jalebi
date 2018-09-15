package com.jalebi.common

import org.apache.hadoop.fs.RemoteIterator
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment

import scala.collection.mutable

object JalebiUtils {

  /**
    * This is the path (suffix) at which the resources are loaded in HDFS for current application.
    */
  def getResourcePath(applicationId: String, resource: String): String = {
    require(applicationId.nonEmpty)
    require(resource.nonEmpty)
    s"jalebi/$applicationId/$resource"
  }

  def getJalebiHomePath(applicationId: String, resource: String): String = {
    require(applicationId.nonEmpty)
    require(resource.nonEmpty)
    s"jalebi/$applicationId/jalebihome/$resource"
  }

  def addToClasspath(env: mutable.HashMap[String, String], value: String): Unit = {
    val classpathKey = Environment.CLASSPATH.name
    val newValue = if (env.contains(classpathKey)) {
      env(classpathKey) + ApplicationConstants.CLASS_PATH_SEPARATOR + value
    } else {
      value
    }
    env.put(classpathKey, newValue)
  }

  def URIForLocalFile(resource: String) = s"file://$resource"

  implicit class RemoteFileIterator[T](i: RemoteIterator[T]) extends Iterator[T] {
    override def hasNext: Boolean = i.hasNext

    override def next(): T = i.next()
  }

}
