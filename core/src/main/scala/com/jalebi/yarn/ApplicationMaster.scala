package com.jalebi.yarn

import java.io.IOException
import java.security.PrivilegedExceptionAction
import java.util
import java.util.Collections
import java.util.concurrent.atomic.AtomicLong

import com.jalebi.utils.{JalebiUtils, Logging, YarnUtils}
import com.jalebi.yarn.executor.ExecutorCommandConstants
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
  val applicationMaster = new ApplicationMaster()

  def main(args: Array[String]): Unit = {
    try {
      println("Test")
      applicationMaster.run(AMArgs(args))
      Thread.sleep(1000000)
    } finally {
      LOGGER.info(s"Unregistering Application Master")
      applicationMaster.finish()
    }
  }
}

class ApplicationMaster extends Logging {
  var amArgs: AMArgs = _
  var amrmClient: AMRMClientAsync[ContainerRequest] = _
  var nmClient: NMClientAsync = _
  var containerStateManager: ContainerStateManager = _

  private val containerMemory = 256
  private val containerVCores = 1
  private val containerType = ExecutionType.GUARANTEED

  private var conf: YarnConfiguration = _
  private val appMasterHostname: String = NetUtils.getHostname
  private val appMasterHostPort: Integer = 8092

  private val launchThreads = ListBuffer[Thread]()

  private val executorIDCounter = new AtomicLong(0)

  def newExecutorID = s"${ExecutorCommandConstants.executorPrefix}_${executorIDCounter.getAndIncrement()}"

  lazy val amContainerId: ContainerId = {
    val containerIdString = System.getenv.get(ApplicationConstants.Environment.CONTAINER_ID.toString)
    if (containerIdString == null) {
      // container id should always be set in the env by the framework
      throw new IllegalArgumentException("ContainerId not set in the environment")
    }
    val containerId = ContainerId.fromString(containerIdString)
    //    val appAttemptID = containerId.getApplicationAttemptId
    containerId
  }

  @throws[YarnException]
  @throws[IOException]
  @throws[InterruptedException]
  def run(args: AMArgs): Integer = {
    this.amArgs = args
    val numberOfContainersNeeded = 3
    LOGGER.info("Inside Application Master.")

    conf = new YarnConfiguration()

    LOGGER.info(s"Current User: ${UserGroupInformation.getCurrentUser}")
    LOGGER.info(s"Current User Credentials: ${UserGroupInformation.getCurrentUser.getCredentials}")

    containerStateManager = ContainerStateManager(numberOfContainersNeeded)
    amrmClient = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler(this, containerStateManager))
    amrmClient.init(conf)
    amrmClient.start()

    nmClient = NMClientAsync.createNMClientAsync(NMCallbackHandler(this, containerStateManager))
    nmClient.init(conf)
    nmClient.start()

    LOGGER.info(s"Registering Application $appMasterHostname")
    val response = amrmClient.registerApplicationMaster(appMasterHostname, appMasterHostPort, "")
    LOGGER.info(s"Registered Application $response")
    println(s"Registered Application $response")

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
    //TODO
    println(s"create launch thread $allocatedContainer")
    val executorID = newExecutorID
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
        s" --${ExecutorCommandConstants.driverURL} $driverURL" +
        s" --${AppMasterCommandConstants.applicationId} ${amArgs.getApplicationId}" +
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
