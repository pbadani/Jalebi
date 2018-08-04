package com.jalebi.utils

import com.jalebi.yarn.JalebiAppConstants
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment
import org.apache.hadoop.yarn.api.records.{LocalResource, LocalResourceType, LocalResourceVisibility, URL}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.Records

import scala.collection.mutable

object YarnUtils {

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

  def createEnvironmentVariables(conf: YarnConfiguration): mutable.HashMap[String, String] = {
    val envVariables = mutable.HashMap[String, String]()
    val classPathElementsToAdd = Option(conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH)) match {
      case Some(s) => s.toSeq
      case None => YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH.toSeq
    }
    classPathElementsToAdd.foreach { c =>
      JalebiUtils.addPathToEnvironment(envVariables, Environment.CLASSPATH.name, c.trim)
    }
    Seq(JalebiAppConstants.jalebiArtifact).foreach { c =>
      JalebiUtils.addPathToEnvironment(envVariables, Environment.CLASSPATH.name, c.trim)
    }
    envVariables
  }
}
