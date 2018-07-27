package com.jalebi.yarn

import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

import com.jalebi.yarn.handler.{AMRMCallbackHandler, NMCallbackHandler}
import org.apache.hadoop.net.NetUtils
import org.apache.hadoop.security.UserGroupInformation
import org.apache.hadoop.yarn.api.records._
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest
import org.apache.hadoop.yarn.client.api.async.{AMRMClientAsync, NMClientAsync}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.hadoop.yarn.exceptions.YarnException
import org.slf4j.LoggerFactory

import scala.collection.mutable

object ApplicationMaster {

  private val LOG = LoggerFactory.getLogger(ApplicationMaster.getClass.getName)

  val containerMemory = 256
  val containerVCores = 1
  val containerType = ExecutionType.GUARANTEED

  val shellCommand = "echo"
  val shellArgs = "'abc'"
  val shellEnvironment = mutable.Map.empty[String, String]

  val numberOfCompletedContainers = new AtomicInteger()

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
    LOG.info("Inside Application Master.")

    val conf = new YarnConfiguration()

    val currentUser = UserGroupInformation.getCurrentUser
    LOG.info(s"Current User: $currentUser")
    val credentials = currentUser.getCredentials

    val amrmClient = AMRMClientAsync.createAMRMClientAsync[ContainerRequest](1000, AMRMCallbackHandler())
    amrmClient.init(conf)
    amrmClient.start()

    val nmClient = NMClientAsync.createNMClientAsync(NMCallbackHandler())
    nmClient.init(conf)
    nmClient.start()

    val appMasterHostname = NetUtils.getHostname

    val response = amrmClient.registerApplicationMaster(appMasterHostname, 8092, "")
    val maxMemory = response.getMaximumResourceCapability.getMemorySize
    val maxCores = response.getMaximumResourceCapability.getVirtualCores
    LOG.info(s"Max memory: $maxMemory, Max cores: $maxCores")

    val r = amrmClient.getAvailableResources

    val previousAMRunningContainers = response.getContainersFromPreviousAttempts

    (1 to numberOfContainersNeeded).foreach(_ => {
      val resourceCapability = createResourceCapability()
      val containerRequest = new ContainerRequest(resourceCapability, null, null, null)
      amrmClient.addContainerRequest(containerRequest)
    })

    0
  }

  private def createResourceCapability(): Resource = {
    Resource.newInstance(containerMemory, containerVCores)
  }

  def finish(): Unit = {

  }
}
