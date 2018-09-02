package com.jalebi.yarn

import java.util
import java.util.Collections

import com.jalebi.common.{JalebiUtils, Logging, URIBuilder, YarnUtils}
import com.jalebi.yarn.CommandConstants.AppMaster
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
    val appclientArgs: ApplicationClientArgs = ApplicationClientArgs(args)
    val application = yarnClient.createApplication()
    val appContext = createApplicationSubmissionContext(application, conf, appclientArgs)
    //    appContext.setApplicationTimeouts(util.Map[ApplicationTimeoutType, Long])
    LOGGER.info(s"Application ID - ${appContext.getApplicationId}")

    val applicationId = yarnClient.submitApplication(appContext)
    val report = yarnClient.getApplicationReport(applicationId)
    println(s"Report: $report")
    //    yarnClient.signalToContainer(application, SignalContainerCommand.GRACEFUL_SHUTDOWN)
  }

  private def createApplicationSubmissionContext(app: YarnClientApplication, conf: YarnConfiguration, appclientArgs: ApplicationClientArgs) = {
    val appContext = app.getApplicationSubmissionContext
    val container = createApplicationMasterContext(conf, appclientArgs, appContext.getApplicationId.toString)
    appContext.setApplicationName(JalebiAppConstants.applicationName)
    appContext.setAMContainerSpec(container)
    appContext.setResource(applicationMasterCapacity)
    appContext
  }

  private def createApplicationMasterContext(conf: YarnConfiguration, clientArgs: ApplicationClientArgs, applicationId: String) = {
    val amContainer: ContainerLaunchContext = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      s"scala ${clientArgs.getClientClass}" +
        //        s"--${CommandConstants.AppMaster.applicationId} $applicationId " +
        //        s"--${CommandConstants.AppMaster.jarPath} ${appclientArgs.getJarPath}" +
        s" 1> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stdout" +
        s" 2> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stderr"
    ).asJava)
    //Set the resource localization for the client jar and for dependencies in 'jalebihome'.
    val clientJar = localizeClientJar(amContainer, conf, clientArgs.getJarPath, applicationId)
    val dependencies = localizeDependencies(amContainer, conf, clientArgs.getJalebiHome, applicationId)
    dependencies.putAll(clientJar)
    amContainer.setLocalResources(dependencies)
    amContainer.setEnvironment(setupEnvironment(conf, clientArgs.getJarPath, applicationId))
    amContainer
  }

  private def setupEnvironment(conf: YarnConfiguration, jarPath: String, applicationId: String): util.Map[String, String] = {
    val appMasterVariables = Map(
      AppMaster.applicationId -> applicationId,
      AppMaster.jarPath -> jarPath
    )
    YarnUtils.createEnvironmentVariables(conf, appMasterVariables).asJava
  }

  private def localizeClientJar(amContainer: ContainerLaunchContext, conf: YarnConfiguration, jarPath: String, applicationId: String): util.Map[String, LocalResource] = {
    //Put a copy of the resource in HDFS for this application and then localize it from there.
    val fs = FileSystem.get(conf)
    val sourcePath = new Path(URIBuilder.forLocalFile(jarPath))
    val destPath = new Path(fs.getHomeDirectory, JalebiUtils.getResourcePath(applicationId, JalebiAppConstants.jalebiArtifact))
    fs.copyFromLocalFile(sourcePath, destPath)
    LOGGER.info(s"Copied resource $jarPath to HDFS destination ${destPath.getParent}/${destPath.getName}")
    val resource = YarnUtils.createFileResource(fs, destPath)
    Collections.singletonMap(JalebiAppConstants.jalebiArtifact, resource)
  }

  private def localizeDependencies(amContainer: ContainerLaunchContext, conf: YarnConfiguration, jalebiHome: String, applicationId: String): util.Map[String, LocalResource] = {
    import com.jalebi.common.JalebiUtils._
    val fs = FileSystem.get(conf)
    fs.listFiles(new Path(URIBuilder.forLocalFile(jalebiHome)), true)
      .map(fileStatus => {
        val fileName = fileStatus.getPath.getName
        val destPath = new Path(fs.getHomeDirectory, JalebiUtils.getJalebiHomePath(applicationId, fileName))
        fs.copyFromLocalFile(fileStatus.getPath, destPath)
        LOGGER.info(s"Copied resource $fileName to HDFS destination ${destPath.getParent}/${destPath.getName}")
        (fileName, YarnUtils.createFileResource(fs, destPath))
      }).toMap.asJava
  }

  private def applicationMasterCapacity = {
    val resource = Records.newRecord(classOf[Resource])
    resource.setMemorySize(300)
    resource.setVirtualCores(1)
    resource
  }
}
