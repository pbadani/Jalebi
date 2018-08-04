package com.jalebi.yarn

import java.util.Collections

import com.jalebi.utils.{JalebiUtils, Logging, URIBuilder, YarnUtils}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.{YarnClient, YarnClientApplication}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.Records

import scala.collection.JavaConverters._

object ApplicationClient extends Logging {

  def main(args: Array[String]): Unit = {
    val yarnClient = YarnClient.createYarnClient()
    val conf = new YarnConfiguration()
    conf.set("fs.defaultFS", "hdfs://localhost:8020")
    yarnClient.init(conf)
    yarnClient.start()
    val jarPath = args(0)

    val application: YarnClientApplication = yarnClient.createApplication()
    val appContext = createApplicationSubmissionContext(application, conf, jarPath)
    //    appContext.setApplicationTimeouts(util.Map[ApplicationTimeoutType, Long])
    LOGGER.info(s"Application ID - ${appContext.getApplicationId}")

    val applicationId = yarnClient.submitApplication(appContext)
    val report = yarnClient.getApplicationReport(applicationId)
    println(s"Report: $report")
    //    yarnClient.signalToContainer(application, SignalContainerCommand.GRACEFUL_SHUTDOWN)
  }

  private def createApplicationSubmissionContext(app: YarnClientApplication, conf: YarnConfiguration, jarPath: String) = {
    val appContext = app.getApplicationSubmissionContext
    val container = createApplicationMasterContext(conf, jarPath, appContext.getApplicationId.toString)
    appContext.setApplicationName(JalebiAppConstants.applicationName)
    appContext.setAMContainerSpec(container)
    appContext.setResource(applicationMasterCapacity)
    appContext
  }

  private def createApplicationMasterContext(conf: YarnConfiguration, jarPath: String, applicationId: String) = {
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      s"scala com.jalebi.yarn.ApplicationMaster " +
        s"--${AppMasterCommandConstants.applicationId} $applicationId " +
        s"--${AppMasterCommandConstants.jarPath} $jarPath" +
        s" 1> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stdout" +
        s" 2> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(JalebiAppConstants.jalebiArtifact, createLocalResource(conf, jarPath, applicationId)))
    amContainer.setEnvironment(YarnUtils.createEnvironmentVariables(conf).asJava)
    amContainer
  }

  private def createLocalResource(conf: YarnConfiguration, jarPath: String, applicationId: String): LocalResource = {
    //Put a copy of the resource in HDFS for this application and then localize it from there.
    val fs = FileSystem.get(conf)
    val sourcePath = new Path(URIBuilder.forLocalFile(jarPath))
    val destPath = new Path(fs.getHomeDirectory, JalebiUtils.getResourcePath(applicationId, JalebiAppConstants.jalebiArtifact))
    fs.copyFromLocalFile(sourcePath, destPath)
    LOGGER.info(s"Copied resource $jarPath to HDFS destination ${destPath.getParent}/${destPath.getName}")

    YarnUtils.createFileResource(fs, destPath)
  }

  private def applicationMasterCapacity = {
    val resource = Records.newRecord(classOf[Resource])
    resource.setMemorySize(300)
    resource.setVirtualCores(1)
    resource
  }
}
