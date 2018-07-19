package com.jalebi.yarn

import java.util.Collections

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.{YarnClient, YarnClientApplication}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.{ConverterUtils, Records}

import scala.collection.JavaConverters._

object Client {
  def main(args: Array[String]): Unit = {
    val conf = new YarnConfiguration()
    val yarnClient = YarnClient.createYarnClient()
    yarnClient.init(conf)
    yarnClient.start()

    val jarPath = args(0)

    val app = yarnClient.createApplication()
    val amContainer: ContainerLaunchContext = createApplicationMasterContext(yarnClient, jarPath, conf)
    val appContext: ApplicationSubmissionContext = createApplicationSubmissionContext(app, amContainer)
    val applicationID = appContext.getApplicationId
    println(s"Application ID - $applicationID")

    yarnClient.submitApplication(appContext)
  }

  private def createApplicationSubmissionContext(app: YarnClientApplication, amContainer: ContainerLaunchContext) = {
    val appContext = app.getApplicationSubmissionContext
    appContext.setApplicationName("Jalebi")
    appContext.setAMContainerSpec(amContainer)
    appContext.setResource(createCapacity)
    appContext
  }

  private def createApplicationMasterContext(yarnClient: YarnClient, jarPath: String, conf: YarnConfiguration) = {
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      "scala -cp graph.jar com.jalebi.yarn.ApplicationMaster" +
        " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
        " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
    ).asJava)

    amContainer.setLocalResources(Collections.singletonMap("graph.jar", createLocalResource(conf)))
    val env = collection.mutable.Map[String, String]()
    amContainer.setEnvironment(env.asJava)
    amContainer
  }

  private def createLocalResource(conf: YarnConfiguration) = {
    val appMasterJar = Records.newRecord(classOf[LocalResource])
    val p = new Path("file:///Users/paragb/home/code/Jalebi/graph/publish/graph.jar")
    val jarStat = FileSystem.get(conf).getFileStatus(p)
    appMasterJar.setResource(ConverterUtils.getYarnUrlFromPath(p))
    appMasterJar.setSize(jarStat.getLen)
    appMasterJar.setTimestamp(jarStat.getModificationTime)
    //    appMasterJar.setResource(URL.newInstance("file", null, 0, "/Users/paragb/home/code/Jalebi/graph/src/main/resources/deploy/graph.jar"))
    //    appMasterJar.setResource(URL.newInstance("file", null, 0, "/Users/paragb/home/code/Jalebi/graph/src/main/resources/deploy/graph.jar"))
    appMasterJar.setType(LocalResourceType.FILE)
    appMasterJar.setVisibility(LocalResourceVisibility.APPLICATION)
    appMasterJar
  }

  private def createCapacity = {
    val resource = Records.newRecord(classOf[Resource])
    resource.setMemory(300)
    resource.setVirtualCores(1)
    resource
  }
}
