package com.jalebi.yarn

import java.io.IOException
import java.security.PrivilegedExceptionAction
import java.util
import java.util.Collections
import java.util.concurrent.atomic.AtomicLong

import com.jalebi.utils.{JalebiUtils, Logging, YarnUtils}
import com.jalebi.yarn.handler.{AMRMCallbackHandler, NMCallbackHandler}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.net.NetUtils
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

  def main(args: Array[String]): Unit = {
    var applicationMaster: Option[ApplicationMaster] = None
    try {
      applicationMaster = Some(new ApplicationMaster(ApplicationMasterArgs(args)))
      applicationMaster.get.run()
      Thread.sleep(1000000)
    } finally {
      if (applicationMaster.isDefined) {
        LOGGER.info(s"Unregistering Application Master")
        applicationMaster.get.finish()
      }
    }
  }
}

class ApplicationMaster(amArgs: ApplicationMasterArgs) extends Logging {
  val numberOfContainersNeeded = 3
  val containerStateManager = ContainerStateManager(numberOfContainersNeeded)

  val amrmClient: AMRMClientAsync[ContainerRequest] = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler(this, containerStateManager))
  val nmClient: NMClientAsync = NMClientAsync.createNMClientAsync(NMCallbackHandler(this, containerStateManager))

  private val conf = new YarnConfiguration()

  private val containerMemory = 256
  private val containerVCores = 1

  private val appMasterHostname: String = NetUtils.getHostname
  private val appMasterHostPort: Integer = 8092

  private val launchThreads = ListBuffer[Thread]()
  private val executorIDCounter = new AtomicLong(0)

  def newExecutorId = s"${CommandConstants.Executor.executorPrefix}_${executorIDCounter.getAndIncrement()}"

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

    amrmClient.init(conf)
    amrmClient.start()

    nmClient.init(conf)
    nmClient.start()

    val response = amrmClient.registerApplicationMaster(appMasterHostname, appMasterHostPort, "")
    LOGGER.info(s"Registered Application $response")

    val maxMemory = response.getMaximumResourceCapability.getMemorySize
    val maxCores = response.getMaximumResourceCapability.getVirtualCores
    LOGGER.info(s"Max memory: $maxMemory, Max cores: $maxCores")

    (1 to numberOfContainersNeeded).foreach(_ => {
      val resourceCapability = createResourceCapability()
      val resourcePriority = createResourcePriority()
      val containerRequest = new ContainerRequest(resourceCapability, null, null, resourcePriority)
      LOGGER.info(s"Container request $containerRequest")
      amrmClient.addContainerRequest(containerRequest)
    })

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

  def createLaunchContainerThread(allocatedContainer: Container): (Thread, String) = {
    val executorID = newExecutorId
    val thread = new Thread(() => {
      val containerLaunchContext = createExecutorContext(conf)
      LOGGER.info(s"Starting container at: " +
        s" | Container Id: ${allocatedContainer.getId}" +
        s" | Node Id: ${allocatedContainer.getNodeId}" +
        s" | Node Address: ${allocatedContainer.getNodeHttpAddress}".stripMargin('|'))
      nmClient.startContainerAsync(allocatedContainer, containerLaunchContext)
    })
    launchThreads += thread
    (thread, executorID)
  }

  private def createExecutorContext(conf: YarnConfiguration) = {
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    val driverURL = s"$appMasterHostname:$appMasterHostPort"
    amContainer.setCommands(List(
      s"scala com.jalebi.yarn.executor.Executor" +
        s" --${CommandConstants.Executor.driverURL} $driverURL" +
        s" --${CommandConstants.AppMaster.applicationId} ${amArgs.getApplicationId}" +
        s" 1> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stdout" +
        s" 2> ${ApplicationConstants.LOG_DIR_EXPANSION_VAR}/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(JalebiAppConstants.jalebiArtifact, createLocalResource(conf)))
    amContainer.setEnvironment(YarnUtils.createEnvironmentVariables(conf).asJava)
    amContainer
  }

  private def createResourcePriority(): Priority = {
    Priority.newInstance(1)
  }

  private def doAsUser[T](fn: => T): T = {
    UserGroupInformation.getCurrentUser.doAs(new PrivilegedExceptionAction[T]() {
      override def run: T = fn
    })
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
}
