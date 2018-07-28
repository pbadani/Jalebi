package com.jalebi.yarn

import java.io.IOException
import java.util

import com.jalebi.utils.Logging
import com.jalebi.yarn.handler.{AMRMCallbackHandler, NMCallbackHandler}
import org.apache.hadoop.net.NetUtils
import org.apache.hadoop.security.UserGroupInformation
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest
import org.apache.hadoop.yarn.client.api.async.{AMRMClientAsync, NMClientAsync}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.exceptions.YarnException

import scala.collection.mutable

class ApplicationMaster extends Logging {

  var amrmClient: AMRMClientAsync[ContainerRequest] = _
  var nmClient: NMClientAsync = _

  val containerMemory = 256
  val containerVCores = 1
  val containerType = ExecutionType.GUARANTEED

  val shellCommand = "echo"
  val shellArgs = "'abc'"
  val shellEnvironment = mutable.Map.empty[String, String]

  def main(args: Array[String]): Unit = {
    println("Test")
    //    val amArgs = AMArgs(args)
    System.exit(run(null))
  }

  @throws[YarnException]
  @throws[IOException]
  @throws[InterruptedException]
  def run(args: AMArgs): Integer = {
    val numberOfContainersNeeded = 3
    LOGGER.info("Inside Application Master.")

    val conf = new YarnConfiguration()
    LOGGER.info(s"Current User: ${UserGroupInformation.getCurrentUser}")
    LOGGER.info(s"Current User Credentials: ${UserGroupInformation.getCurrentUser.getCredentials}")

    val containerStateManager = ContainerStateManager(numberOfContainersNeeded)

    amrmClient = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler(this, containerStateManager))
    amrmClient.init(conf)
    amrmClient.start()

    nmClient = NMClientAsync.createNMClientAsync(NMCallbackHandler(this, containerStateManager))
    nmClient.init(conf)
    nmClient.start()

    val appMasterHostname = NetUtils.getHostname
    val response = amrmClient.registerApplicationMaster(appMasterHostname, 8092, "")

    val maxMemory = response.getMaximumResourceCapability.getMemorySize
    val maxCores = response.getMaximumResourceCapability.getVirtualCores
    LOGGER.info(s"Max memory: $maxMemory, Max cores: $maxCores")

    (1 to numberOfContainersNeeded).foreach(_ => {
      val resourceCapability = createResourceCapability()
      val containerRequest = new ContainerRequest(resourceCapability, null, null, null)
      amrmClient.addContainerRequest(containerRequest)
    })

    0
  }

  def releaseContainers(containers: util.List[Container]): Unit = {
    require(amrmClientIsInitialized)
    containers.forEach(container => {
      LOGGER.info(s"Releasing Container with Id: ${container.getId}")
      amrmClient.releaseAssignedContainer(container.getId)
    })
  }

  def createLaunchContainerThread(allocatedContainer: Container, executorId: String): Thread = {
    new Thread(() => {

    })
  }

  private def amrmClientIsInitialized = amrmClient != null

  private def nmClientIsInitialized = nmClient != null

  private def createResourceCapability(): Resource = {
    Resource.newInstance(containerMemory, containerVCores)
  }

  def finish(): Unit = {

  }
}
