package com.jalebi.yarn.handler

import java.util

import com.jalebi.common.Logging
import com.jalebi.yarn.{ApplicationMaster, ContainerStateManager}
import org.apache.hadoop.yarn.api.records.{Container, ContainerStatus, NodeReport, UpdatedContainer}
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.AbstractCallbackHandler

class AMRMCallbackHandler(applicationMaster: ApplicationMaster, containerStateManager: ContainerStateManager) extends AbstractCallbackHandler with Logging {

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
    containerStateManager.containersCompleted(statuses)
  }

  override def getProgress: Float = containerStateManager.getProgress

  override def onNodesUpdated(updatedNodes: util.List[NodeReport]): Unit = {
    updatedNodes.forEach(nodeReport => {
      LOGGER.info(s"Node updated: " +
        s" | Node Id: ${nodeReport.getNodeId}".stripMargin('|'))
    })
  }

  override def onContainersAllocated(containers: util.List[Container]): Unit = {
    containers.forEach(container => {
      LOGGER.info(s"Container Allocated: " +
        s" | Id: ${container.getId}" +
        s" | Allocation request id: ${container.getAllocationRequestId}" +
        s" | Node id: ${container.getNodeId}" +
        s" | Node address: ${container.getNodeHttpAddress}" +
        s" | Execution type: ${container.getExecutionType}".stripMargin('|'))
    })
    if (containerStateManager.areAllContainerRequestsFulfilled()) {
      applicationMaster.releaseContainers(containers)
    } else {
      containerStateManager.containersAllocated(containers)
      containers.forEach(container => {
        val (launchThread, executorId) = applicationMaster.createLaunchContainerThread(container)
        println(s"Launching $executorId")
        LOGGER.info(
          s"""Launching executor on a new container:" +
          " | Jalebi Executor id: $executorId" +
          " | Container id: ${container.getId}" +
          " | Node id: ${container.getNodeId}" +
          " | Node address: ${container.getNodeHttpAddress}" +
          " | Container memory: ${container.getResource.getMemorySize}" +
          " | Container vcores: ${container.getResource.getVirtualCores}""".stripMargin('|'))

        launchThread.start()
        //        applicationMaster.removeContainerRequest(container.getAllocationRequestId)
      })
    }
  }
}

object AMRMCallbackHandler {
  def apply(applicationMaster: ApplicationMaster, containerStateManager: ContainerStateManager): AMRMCallbackHandler = new AMRMCallbackHandler(applicationMaster, containerStateManager)
}
