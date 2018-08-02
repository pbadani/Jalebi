package com.jalebi.yarn

import java.util.Collections

import com.jalebi.utils.{JalebiUtils, Logging, URIBuilder}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.{YarnClient, YarnClientApplication}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.Records

import scala.collection.JavaConverters._
import scala.collection.mutable

object ApplicationClient extends Logging {

  private val artifact = "core.jar"
  private val applicationName = "Jalebi"

  def main(args: Array[String]): Unit = {
    val conf = new YarnConfiguration()
    val yarnClient = YarnClient.createYarnClient()
    yarnClient.init(conf)
    yarnClient.start()
    val jarPath = args(0)

    val application = yarnClient.createApplication()
    val amContainer = createApplicationMasterContext(jarPath, conf)
    val appContext = createApplicationSubmissionContext(application, amContainer)
//    appContext.setApplicationTimeouts(util.Map[ApplicationTimeoutType, Long])
    LOGGER.info(s"Application ID - ${appContext.getApplicationId}")

    val applicationId = yarnClient.submitApplication(appContext)
    val report = yarnClient.getApplicationReport(applicationId)
    println(s"Report: $report")
//    yarnClient.signalToContainer(application, SignalContainerCommand.GRACEFUL_SHUTDOWN)
  }

  private def createApplicationSubmissionContext(app: YarnClientApplication, amContainer: ContainerLaunchContext) = {
    val appContext = app.getApplicationSubmissionContext
    appContext.setApplicationName(applicationName)
    appContext.setAMContainerSpec(amContainer)
    appContext.setResource(amCapacity)
    appContext
  }

  private def createApplicationMasterContext(jarPath: String, conf: YarnConfiguration) = {
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      s"scala com.jalebi.yarn.ApplicationMaster" +
        " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
        " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(artifact, createLocalResource(conf, jarPath)))
    amContainer.setEnvironment(createEnvironmentVariables(conf).asJava)
    amContainer
  }

  private def createLocalResource(conf: YarnConfiguration, jarPath: String): LocalResource = {
    val appMasterJar = Records.newRecord(classOf[LocalResource])
    val path = new Path(URIBuilder.forLocalFile(jarPath))
    val jarStat = FileSystem.get(conf).getFileStatus(path)
    appMasterJar.setResource(URL.fromPath(path))
    appMasterJar.setSize(jarStat.getLen)
    appMasterJar.setTimestamp(jarStat.getModificationTime)
    appMasterJar.setType(LocalResourceType.FILE)
    appMasterJar.setVisibility(LocalResourceVisibility.APPLICATION)
    appMasterJar
  }

  private def amCapacity = {
    val resource = Records.newRecord(classOf[Resource])
    resource.setMemorySize(300)
    resource.setVirtualCores(1)
    resource
  }

  private def createEnvironmentVariables(conf: YarnConfiguration): mutable.HashMap[String, String] = {
    val envVariables = mutable.HashMap[String, String]()
    populateYarnClasspath(conf, envVariables)
  }

  private[yarn] def populateYarnClasspath(conf: Configuration, env: mutable.HashMap[String, String]): mutable.HashMap[String, String] = {
    val classPathElementsToAdd = Option(conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH)) match {
      case Some(s) => s.toSeq
      case None => YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH.toSeq
    }
    classPathElementsToAdd.foreach { c =>
      JalebiUtils.addPathToEnvironment(env, Environment.CLASSPATH.name, c.trim)
    }
    Seq("core.jar").foreach { c =>
      JalebiUtils.addPathToEnvironment(env, Environment.CLASSPATH.name, c.trim)
    }
    env
  }
}
