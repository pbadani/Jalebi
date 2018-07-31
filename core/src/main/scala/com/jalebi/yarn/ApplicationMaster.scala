package com.jalebi.yarn

import java.io.IOException
import java.util
import java.util.Collections
import java.util.concurrent.atomic.AtomicLong

import com.jalebi.executor.ExecutorCommandConstants
import com.jalebi.utils.{Logging, URIBuilder}
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
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object ApplicationMaster extends Logging {
  val applicationMaster = new ApplicationMaster()

  def main(args: Array[String]): Unit = {
    try {
      println("Test")
      applicationMaster.run(null)
    } finally {
      LOGGER.info(s"Unregistering Application Master")
      applicationMaster.finish()
    }
  }
}

class ApplicationMaster extends Logging {
  private val artifact = "core.jar"

  var amrmClient: AMRMClientAsync[ContainerRequest] = _
  var nmClient: NMClientAsync = _

  private val containerMemory = 256
  private val containerVCores = 1
  private val containerType = ExecutionType.GUARANTEED

  private val shellCommand = "echo"
  private val shellArgs = "'abc'"
  private val shellEnvironment = mutable.Map.empty[String, String]

  private var conf: YarnConfiguration = _
  private val appMasterHostname: String = NetUtils.getHostname
  private val appMasterHostPort: Integer = 8092

  private val launchThreads = ListBuffer[Thread]()

  private val executorIDCounter = new AtomicLong(0)
  val newExecutorID = s"${ExecutorCommandConstants.executorPrefix}_${executorIDCounter.getAndIncrement()}"

  @throws[YarnException]
  @throws[IOException]
  @throws[InterruptedException]
  def run(args: AMArgs): Integer = {
    val numberOfContainersNeeded = 3
    LOGGER.info("Inside Application Master.")

    conf = new YarnConfiguration()

    LOGGER.info(s"Current User: ${UserGroupInformation.getCurrentUser}")
    LOGGER.info(s"Current User Credentials: ${UserGroupInformation.getCurrentUser.getCredentials}")

    val containerStateManager = ContainerStateManager(numberOfContainersNeeded)

    amrmClient = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler(this, containerStateManager))
    amrmClient.init(conf)
    amrmClient.start()

    nmClient = NMClientAsync.createNMClientAsync(NMCallbackHandler(this, containerStateManager))
    nmClient.init(conf)
    nmClient.start()

    LOGGER.info(s"Registering Application $appMasterHostname")
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
    //TODO
    val executorID = newExecutorID
    val thread = new Thread(() => {
      val containerLaunchContext = createExecutorContext(artifact, conf)
      nmClient.startContainerAsync(allocatedContainer, containerLaunchContext)
    })
    launchThreads += thread
    (thread, executorID)
  }

  private def createExecutorContext(jarPath: String, conf: YarnConfiguration) = {
    val amContainer = Records.newRecord(classOf[ContainerLaunchContext])
    amContainer.setCommands(List(
      s"scala -cp $artifact com.jalebi.executor.Executor" +
        " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
        " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
    ).asJava)
    amContainer.setLocalResources(Collections.singletonMap(artifact, createLocalResource(conf, jarPath)))
    val env = collection.mutable.Map[String, String]()
    amContainer.setEnvironment(env.asJava)
    amContainer
  }

  private def createResourcePriority(): Priority = {
    Priority.newInstance(1)
  }

  private def createLocalResource(conf: YarnConfiguration, jarPath: String): LocalResource = {
    val applicationJar = Records.newRecord(classOf[LocalResource])
    val path = new Path(URIBuilder.forLocalFile(jarPath))
    val jarStat = FileSystem.get(conf).getFileStatus(path)
    applicationJar.setResource(URL.fromPath(path))
    applicationJar.setSize(jarStat.getLen)
    applicationJar.setTimestamp(jarStat.getModificationTime)
    applicationJar.setType(LocalResourceType.FILE)
    applicationJar.setVisibility(LocalResourceVisibility.APPLICATION)
    applicationJar
  }

  private def amrmClientIsInitialized = amrmClient != null

  private def nmClientIsInitialized = nmClient != null

  private def createResourceCapability(): Resource = {
    Resource.newInstance(containerMemory, containerVCores)
  }

  def finish(): Unit = {
    amrmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, "Jalebi Done", "")
  }
}
