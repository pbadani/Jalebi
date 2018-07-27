package com.jalebi.yarn

import java.util.Collections

import com.jalebi.utils.URIBuilder
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.{YarnClient, YarnClientApplication}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.Records

import scala.collection.JavaConverters._

object ApplicationClient {

  private val artifact = "graph.jar"
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
    println(s"Application ID - ${appContext.getApplicationId}")

    yarnClient.submitApplication(appContext)
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
      s"scala -cp $artifact com.jalebi.yarn.ApplicationMaster" +
        " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
        " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(artifact, createLocalResource(conf, jarPath)))
    val env = collection.mutable.Map[String, String]()
    amContainer.setEnvironment(env.asJava)
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
}
