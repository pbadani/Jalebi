package com.jalebi.yarn

import java.io.IOException
import java.util
import java.util.Collections

import com.jalebi.common.{JalebiUtils, Logging, YarnUtils}
import com.jalebi.context.JalebiContext
import com.jalebi.driver.{ExecutorStateManager, Scheduler}
import com.jalebi.yarn.CommandConstants.{AppMaster, ExecutorConstants}
import com.jalebi.yarn.handler.{AMRMCallbackHandler, NMCallbackHandler}
import org.apache.commons.lang.StringUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.security.UserGroupInformation
import org.apache.hadoop.yarn.api.ApplicationConstants
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest
import org.apache.hadoop.yarn.client.api.async.{AMRMClientAsync, NMClientAsync}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.exceptions.YarnException
import org.apache.hadoop.yarn.util.Records

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

object ApplicationMaster extends Logging {

  def apply(context: JalebiContext): ApplicationMaster = {
    new ApplicationMaster(context, ApplicationMasterArgs.createArgsFromEnvironment())
  }

  def main(args: Array[String]): Unit = {
    var applicationMaster: Option[ApplicationMaster] = None
    try {
      applicationMaster = Some(new ApplicationMaster(null, ApplicationMasterArgs(args)))
      applicationMaster.get.run()
      Thread.sleep(1000000)
    } finally {
      applicationMaster.foreach(am => {
        LOGGER.info(s"Unregistering Application Master.")
        am.finish()
      })
    }
  }
}

class ApplicationMaster(context: JalebiContext, amArgs: ApplicationMasterArgs) extends Scheduler(context) with Logging {
  val numberOfContainersNeeded = 3
  val containerStateManager = ContainerStateManager(numberOfContainersNeeded)
  val executorState: ExecutorStateManager = ExecutorStateManager(context.conf)

  val amrmClient: AMRMClientAsync[ContainerRequest] = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler(this, executorState))
  val nmClient: NMClientAsync = NMClientAsync.createNMClientAsync(NMCallbackHandler(this, executorState))

  private val containerMemory = 256
  private val containerVCores = 1

  val (driverHost, driverPort) = (context.driverHostPort.host, context.driverHostPort.port)

  private val launchThreads = ListBuffer[Thread]()
  lazy val amContainerId: ContainerId = {
    val containerIdString = System.getenv.get(ApplicationConstants.Environment.CONTAINER_ID.toString)
    if (containerIdString == null) {
      // container id should always be set in the env by the framework
      throw new IllegalArgumentException("ContainerId not set in the environment")
    }
    ContainerId.fromString(containerIdString)
  }

  @throws[YarnException]
  @throws[IOException]
  @throws[InterruptedException]
  def run(): Integer = {
    LOGGER.info("Inside Application Master.")
    LOGGER.info(s"Current User: ${UserGroupInformation.getCurrentUser}")
    LOGGER.info(s"Current User Credentials: ${UserGroupInformation.getCurrentUser.getCredentials}")

    amrmClient.init(context.yarnConf)
    amrmClient.start()

    nmClient.init(context.yarnConf)
    nmClient.start()


    val response = amrmClient.registerApplicationMaster(driverHost, driverPort.toInt, StringUtils.EMPTY)
    LOGGER.info(s"Registered Application $response.")

    val maxMemory = response.getMaximumResourceCapability.getMemorySize
    val maxCores = response.getMaximumResourceCapability.getVirtualCores
    LOGGER.info(s"Max memory: $maxMemory, Max cores: $maxCores")

//    (1 to numberOfContainersNeeded).foreach(_ => {
//      val resourceCapability = createResourceCapability()
//      val resourcePriority = createResourcePriority()
//      val containerRequest = new ContainerRequest(resourceCapability, null, null, resourcePriority)
//      LOGGER.info(s"Container request $containerRequest")
//      amrmClient.addContainerRequest(containerRequest)
//    })

    1
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

  def createLaunchContainerThread(allocatedContainer: Container, executorId: String): Thread = {
    val thread = new Thread(() => {
      val containerLaunchContext = createExecutorContext(context.yarnConf, executorId)
      LOGGER.info(s"Starting container at: " +
        s" | Executor id: $executorId" +
        s" | Container Id: ${allocatedContainer.getId}" +
        s" | Node Id: ${allocatedContainer.getNodeId}" +
        s" | Node Address: ${allocatedContainer.getNodeHttpAddress}".stripMargin('|'))
      nmClient.startContainerAsync(allocatedContainer, containerLaunchContext)
    })
    thread.setName(executorId)
    launchThreads += thread
    thread
  }

  private def createExecutorContext(conf: YarnConfiguration, executorId: String) = {
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      s"scala com.jalebi.yarn.executor.Executor" +
        s" --${ExecutorConstants.driverHost} $driverHost" +
        s" --${ExecutorConstants.driverPort} $driverPort" +
        s" --${ExecutorConstants.executorId} $executorId" +
        s" --${AppMaster.applicationId} ${amArgs.getApplicationId}" +
        s" 1> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stdout" +
        s" 2> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(JalebiAppConstants.jalebiArtifact, createLocalResource(conf)))
    amContainer.setEnvironment(YarnUtils.createEnvironmentVariables(conf, Map.empty).asJava)
    amContainer
  }

  private def createResourcePriority(): Priority = {
    Priority.newInstance(1)
  }

  private def createLocalResource(conf: YarnConfiguration): LocalResource = {
    val fs = FileSystem.get(conf)
    val resourcePath = new Path(fs.getHomeDirectory, JalebiUtils.getResourcePath(amArgs.getApplicationId, JalebiAppConstants.jalebiArtifact))
    YarnUtils.createFileResource(fs, resourcePath)
  }

  private def amrmClientIsInitialized = amrmClient != null

  private def nmClientIsInitialized = nmClient != null

  private def createResourceCapability(): Resource = {
    Resource.newInstance(containerMemory, containerVCores)
  }

  def finish(): Unit = {
    if (nmClient != null) {
      containerStateManager.forAllLaunchedContainers((containerId, nodeId) => nmClient.stopContainerAsync(containerId, nodeId))
      nmClient.stop()
    }
    if (amrmClient != null) {
      amrmClient.releaseAssignedContainer(amContainerId)
      amrmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "Jalebi Done", "")
      amrmClient.stop()
    }
  }

  override def startExecutors(executorIds: Set[String]): Unit = {
    executorIds.foreach(executorId => {
      executorState.addExecutor(executorId)
      val resourceCapability = createResourceCapability()
      val resourcePriority = createResourcePriority()
      val containerRequest = new ContainerRequest(resourceCapability, null, null, resourcePriority)
      LOGGER.info(s"Container request $containerRequest")
      amrmClient.addContainerRequest(containerRequest)
    })
  }

  override def shutExecutors(executorIds: Set[String]): Unit = ???

  override def shutAllExecutors(): Unit = ???
}
