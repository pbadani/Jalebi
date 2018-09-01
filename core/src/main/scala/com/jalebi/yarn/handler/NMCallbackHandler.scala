package com.jalebi.yarn.handler

import java.nio.ByteBuffer
import java.util

import com.jalebi.common.Logging
import com.jalebi.driver.ExecutorStateManager
import com.jalebi.yarn.ApplicationMaster
import org.apache.hadoop.yarn.api.records.{ContainerId, ContainerStatus, Resource}
import org.apache.hadoop.yarn.client.api.async.NMClientAsync.AbstractCallbackHandler

class NMCallbackHandler(applicationMaster: ApplicationMaster, executorStateManager: ExecutorStateManager) extends AbstractCallbackHandler with Logging {

  override def onGetContainerStatusError(containerId: ContainerId, t: Throwable): Unit = ???

  override def onContainerStatusReceived(containerId: ContainerId, containerStatus: ContainerStatus): Unit = ???

  override def onContainerResourceIncreased(containerId: ContainerId, resource: Resource): Unit = {
    LOGGER.info(s"Container resource increased:" +
      s" | Container Id: $containerId" +
      s" | Container memory: ${resource.getMemorySize}" +
      s" | Container vcores: ${resource.getVirtualCores}".stripMargin('|'))
  }

  override def onStopContainerError(containerId: ContainerId, t: Throwable): Unit = {
    LOGGER.error(s"Error while stopping container:" +
      s" | Container Id: $containerId" +
      s" | Error: ${t.getMessage}".stripMargin('|'))
  }

  override def onContainerStopped(containerId: ContainerId): Unit = {
    LOGGER.info(s"Container stopped:" +
      s" | Container Id: $containerId".stripMargin('|'))
    //TODO
    //    containerStateManager.containerCompleted(containerId)
  }

  override def onIncreaseContainerResourceError(containerId: ContainerId, t: Throwable): Unit = {
    LOGGER.error(s"Error while increasing container resource:" +
      s" | Container Id: $containerId" +
      s" | Error: ${t.getMessage}".stripMargin('|'))
  }

  override def onStartContainerError(containerId: ContainerId, t: Throwable): Unit = {
    LOGGER.error(s"Error while starting container:" +
      s" | Container Id: $containerId" +
      s" | Error: ${t.getMessage}".stripMargin('|'))
    //TODO
  }

  override def onUpdateContainerResourceError(containerId: ContainerId, t: Throwable): Unit = {
    LOGGER.error(s"Error while updating container resource:" +
      s" | Container Id: $containerId" +
      s" | Error: ${t.getMessage}".stripMargin('|'))
  }

  override def onContainerStarted(containerId: ContainerId, allServiceResponse: util.Map[String, ByteBuffer]): Unit = {
    LOGGER.info(s"Container launched $containerId")
    //TODO
    //    containerStateManager.containerLaunched(containerId)
  }

  override def onContainerResourceUpdated(containerId: ContainerId, resource: Resource): Unit = {
    LOGGER.info(s"Container resource updated:" +
      s" | Container Id: $containerId" +
      s" | Container memory: ${resource.getMemorySize}" +
      s" | Container vcores: ${resource.getVirtualCores}".stripMargin('|'))
  }
}

object NMCallbackHandler {
  def apply(applicationMaster: ApplicationMaster, executorStateManager: ExecutorStateManager): NMCallbackHandler = new NMCallbackHandler(applicationMaster, executorStateManager)
}
