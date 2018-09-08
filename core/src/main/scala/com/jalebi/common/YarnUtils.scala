package com.jalebi.common

import com.jalebi.yarn.JalebiAppConstants
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.Records

import scala.collection.mutable

object YarnUtils {

  def createResourcePriority(priority: Int = 1): Priority = Priority.newInstance(priority)

  def createFileResource(fs: FileSystem, destPath: Path): LocalResource = {
    val resource = Records.newRecord(classOf[LocalResource])
    val resourceStat = fs.getFileStatus(destPath)
    resource.setResource(URL.fromPath(destPath))
    resource.setSize(resourceStat.getLen)
    resource.setTimestamp(resourceStat.getModificationTime)
    resource.setType(LocalResourceType.FILE)
    resource.setVisibility(LocalResourceVisibility.APPLICATION)
    resource
  }

  def createEnvironmentVariables(conf: YarnConfiguration, resourceNames: Set[String], additional: Map[String, String]): mutable.HashMap[String, String] = {
    val envVariables = mutable.HashMap[String, String]()
    resourceNames.foreach { c =>
      JalebiUtils.addToClasspath(envVariables, c.trim)
    }
    additional.foreach {
      case (key, value) => envVariables.put(key, value)
    }
    (Option(conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH)) match {
      case Some(s) => s.toSeq
      case None => YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH.toSeq
    }).foreach { c =>
      JalebiUtils.addToClasspath(envVariables, c.trim)
    }
    envVariables
  }
}
