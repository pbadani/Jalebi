package com.jalebi.yarn

import java.util
import java.util.Collections

import akka.actor.{ActorRef, Props}
import com.jalebi.common.{JalebiUtils, Logging, YarnUtils}
import com.jalebi.driver.Scheduler
import com.jalebi.hdfs.HostPort
import com.jalebi.message._
import com.jalebi.yarn.CommandConstants.{AppMaster, ExecutorConstants}
import com.jalebi.yarn.handler.{AMRMCallbackHandler, NMCallbackHandler}
import org.apache.commons.lang.StringUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest
import org.apache.hadoop.yarn.client.api.async.{AMRMClientAsync, NMClientAsync}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.util.Records

import scala.collection.JavaConverters._

case class ApplicationMaster(amArgs: ApplicationMasterArgs, applicationId: String, stateMonitorRef: ActorRef, driverHostPort: HostPort) extends Scheduler with Logging {

  override def receive: Receive = {
    case StartExecutors(executorIds) =>
      executorIds.zipWithIndex.foreach {
        case (executorId, requestId) =>
          val resourceCapability = createResourceCapability()
          val resourcePriority = YarnUtils.createResourcePriority()
          val containerRequest = new ContainerRequest(resourceCapability, null, null, resourcePriority, requestId)
          LOGGER.info(s"Container request $containerRequest")
          amrmClient.addContainerRequest(containerRequest)
          stateMonitorRef ! ContainerRequested(executorId, requestId)
      }
    case c@ContainerAllocated(_) => stateMonitorRef ! c
    case LaunchContainer(executorId, allocatedContainer) =>
      val containerLaunchContext = createExecutorContext(yarnConf, executorId)
      LOGGER.info(s"Starting container at: " +
        s" | Executor id: $executorId" +
        s" | Container Id: ${allocatedContainer.getId}" +
        s" | Node Id: ${allocatedContainer.getNodeId}" +
        s" | Node Address: ${allocatedContainer.getNodeHttpAddress}".stripMargin('|'))
      nmClient.startContainerAsync(allocatedContainer, containerLaunchContext)
    //    case StopExecutors =>
    //      refs.foreach {
    //        case (_, ref) => ref ! ShutExecutors
    //      }
  }

  val yarnConf = new YarnConfiguration()
  val amrmClient: AMRMClientAsync[ContainerRequest] = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler(context.self))
  amrmClient.init(yarnConf)
  amrmClient.start()

  val nmClient: NMClientAsync = NMClientAsync.createNMClientAsync(NMCallbackHandler(context.self))
  nmClient.init(yarnConf)
  nmClient.start()

  val response: RegisterApplicationMasterResponse = amrmClient.registerApplicationMaster(driverHostPort.host, driverHostPort.port.toInt, StringUtils.EMPTY)
  LOGGER.info(s"Registered Application $response.")

  lazy val amContainerId: ContainerId = {
    val containerIdString = System.getenv.get(ApplicationConstants.Environment.CONTAINER_ID.toString)
    if (containerIdString == null) {
      // container id should always be set in the env by the framework
      throw new IllegalArgumentException("ContainerId not set in the environment")
    }
    ContainerId.fromString(containerIdString)
  }

  def releaseContainers(containers: util.List[Container]): Unit = {
    require(amrmClientIsInitialized)
    containers.forEach(container => {
      LOGGER.info(s"Releasing Container with Id: ${container.getId}")
      amrmClient.releaseAssignedContainer(container.getId)
    })
  }

  def removeContainerRequest(allocationRequestId: Long): Unit = {
    require(amrmClientIsInitialized)
    amrmClient.getMatchingRequests(allocationRequestId).forEach(request => {
      LOGGER.info(s"Removing container request: " +
        s" | Allocation request Id: $allocationRequestId".stripMargin('|'))
      amrmClient.removeContainerRequest(request)
    })
  }

  private def createExecutorContext(conf: YarnConfiguration, executorId: String) = {
    LOGGER.info(s"Executor Context ${driverHostPort.host}, ${driverHostPort.port} $executorId")
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      s"/usr/local/bin/scala com.jalebi.executor.Executor" +
        s" --${ExecutorConstants.driverHost} ${driverHostPort.host}" +
        s" --${ExecutorConstants.driverPort} ${driverHostPort.port}" +
        s" --${ExecutorConstants.executorId} $executorId" +
        s" --${AppMaster.applicationId} ${amArgs.getApplicationId}" +
        s" 1> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stdout" +
        s" 2> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(JalebiAppConstants.jalebiArtifact, createLocalResource(conf)))
    amContainer.setEnvironment(YarnUtils.createEnvironmentVariables(conf, Set(JalebiAppConstants.jalebiArtifact), Map.empty).asJava)
    amContainer
  }

  private def createLocalResource(conf: YarnConfiguration): LocalResource = {
    val fs = FileSystem.get(conf)
    val resourcePath = new Path(fs.getHomeDirectory, JalebiUtils.getResourcePath(amArgs.getApplicationId, JalebiAppConstants.jalebiArtifact))
    YarnUtils.createFileResource(fs, resourcePath)
  }

  private def amrmClientIsInitialized = amrmClient != null

  private def createResourceCapability() = Resource.newInstance(256, 1)

  def finish(): Unit = {
    if (nmClient != null) {
      nmClient.stop()
    }
    if (amrmClient != null) {
      amrmClient.releaseAssignedContainer(amContainerId)
      amrmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "Jalebi Done", "")
      amrmClient.stop()
    }
  }
}

object ApplicationMaster extends Logging {

  def props(applicationId: String, stateMonitorRef: ActorRef, driverHostPort: HostPort) = Props(ApplicationMaster(ApplicationMasterArgs.createArgsFromEnvironment(), applicationId, stateMonitorRef, driverHostPort))

  def name() = "AppMaster"
}