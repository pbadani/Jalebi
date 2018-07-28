package com.jalebi.yarn.handler

import java.util
import java.util.stream.Collectors

import com.jalebi.executor.Executor
import com.jalebi.utils.Logging
import com.jalebi.yarn.{ApplicationMaster, ContainerStateManager}
import org.apache.hadoop.yarn.api.records.{Container, ContainerStatus, NodeReport, UpdatedContainer}
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.AbstractCallbackHandler

class AMRMCallbackHandler(applicationMaster: ApplicationMaster, containerStateManager: ContainerStateManager) extends AbstractCallbackHandler with Logging {

  override def onContainersUpdated(containers: util.List[UpdatedContainer]): Unit = ???

  override def onError(e: Throwable): Unit = ???

  override def onShutdownRequest(): Unit = ???

  override def onContainersCompleted(statuses: util.List[ContainerStatus]): Unit = {
    containerStateManager.containersCompleted(statuses)
  }

  override def getProgress: Float = ???

  override def onNodesUpdated(updatedNodes: util.List[NodeReport]): Unit = ???



  override def onContainersAllocated(containers: util.List[Container]): Unit = {
    LOGGER.info(s"Containers Allocated: ${containers.stream().map(container => container.getId).collect(Collectors.joining(", "))}")
    if (containerStateManager.areAllContainerRequestsFulfilled()) {
      applicationMaster.releaseContainers(containers)
    } else {
      containerStateManager.containersAllocated(containers)
      containers.forEach(container => {
        val executorID = Executor.newExecutorID

        LOGGER.info(s"Launching executor on a new container:" +
          s" | Jalebi Executor id: $executorID" +
          s" | Container id: ${container.getId}" +
          s" | Node id: ${container.getNodeId}" +
          s" | Node address: ${container.getNodeHttpAddress}" +
          s" | Container memory: ${container.getResource.getMemorySize}" +
          s" | Container vcores: ${container.getResource.getVirtualCores}".stripMargin('|'))

        val launchThread = createLaunchContainerThread(allocatedContainer, yarnShellId)
      })
    }
  }
}

object AMRMCallbackHandler {
  def apply(applicationMaster: ApplicationMaster, containerStateManager: ContainerStateManager): AMRMCallbackHandler = new AMRMCallbackHandler(applicationMaster, containerStateManager)
}
