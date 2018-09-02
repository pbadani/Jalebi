package com.jalebi.yarn.handler

import java.util

import com.jalebi.common.Logging
import com.jalebi.driver.ExecutorStateManager
import com.jalebi.yarn.ApplicationMaster
import org.apache.hadoop.yarn.api.records.{Container, ContainerStatus, NodeReport, UpdatedContainer}
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.AbstractCallbackHandler

class AMRMCallbackHandler(applicationMaster: ApplicationMaster, executorStateManager: ExecutorStateManager) extends AbstractCallbackHandler with Logging {

  override def onContainersUpdated(containers: util.List[UpdatedContainer]): Unit = {
    containers.forEach(container => {
      LOGGER.info(s"Container resource updated:" +
        s" | Container Id: ${container.getContainer.getId}" +
        s" | Resource update type: ${container.getUpdateType}".stripMargin('|'))
    })
  }

  override def onError(e: Throwable): Unit = {
    LOGGER.error(s"Error in AM RM callback handler: ${e.getMessage}")
  }

  override def onShutdownRequest(): Unit = {
    LOGGER.error(s"Invoked shutdown request.")
  }

  override def onContainersCompleted(statuses: util.List[ContainerStatus]): Unit = {
    statuses.forEach(status => {
      LOGGER.info(s"Container resource completed:" +
        s" | Container Id: ${status.getContainerId}" +
        s" | Container Substate: ${status.getContainerSubState}".stripMargin('|'))
    })
    //TODO
    //    containerStateManager.containersCompleted(statuses)
  }

  override def getProgress: Float = {
    //    containerStateManager.getProgress
    70F
  }

  override def onNodesUpdated(updatedNodes: util.List[NodeReport]): Unit = {
    updatedNodes.forEach(nodeReport => {
      LOGGER.info(s"Node updated: " +
        s" | Node Id: ${nodeReport.getNodeId}".stripMargin('|'))
    })
  }

  override def onContainersAllocated(containers: util.List[Container]): Unit = {
    //    if (containerStateManager.areAllContainerRequestsFulfilled()) {
    //      applicationMaster.releaseContainers(containers)
    //    } else {
    //    containerStateManager.containersAllocated(containers)
    containers.forEach(container => {
      val executorId = executorStateManager.findExecutorToAssignContainer(container)
      if (executorId.isDefined) {
        val launchThread = applicationMaster.createLaunchContainerThread(executorId.get, container)
        LOGGER.info(
          s"""Launching executor on a new container:" +
          " | Jalebi Executor id: $executorId" +
          " | Container id: ${container.getId}" +
          " | Node id: ${container.getNodeId}" +
          " | Node address: ${container.getNodeHttpAddress}" +
          " | Container memory: ${container.getResource.getMemorySize}" +
          " | Container vcores: ${container.getResource.getVirtualCores}""".stripMargin('|'))
        launchThread.start()
      } else {
        LOGGER.info(
          s"""No executor to allocate. Removing container request:" +
          " | Container id: ${container.getId}" +
          " | Node id: ${container.getNodeId}" +
          " | Node address: ${container.getNodeHttpAddress}" +
          " | Container memory: ${container.getResource.getMemorySize}" +
          " | Container vcores: ${container.getResource.getVirtualCores}""".stripMargin('|'))
        applicationMaster.removeContainerRequest(container.getAllocationRequestId)
      }
    })
    //    }
  }
}

object AMRMCallbackHandler {
  def apply(applicationMaster: ApplicationMaster, executorStateManager: ExecutorStateManager): AMRMCallbackHandler = new AMRMCallbackHandler(applicationMaster, executorStateManager)
}
