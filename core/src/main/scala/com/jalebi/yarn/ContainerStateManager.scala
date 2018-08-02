package com.jalebi.yarn

import java.util
import java.util.concurrent.atomic.AtomicInteger

import org.apache.hadoop.yarn.api.records.{Container, ContainerId, ContainerStatus, NodeId}

import scala.collection.mutable

case class ContainerStateManager(numOfContainersNeeded: Int) {

  private val containerIdToNodeIdMap = new mutable.HashMap[ContainerId, NodeId]()

  private val containersAllocated = new mutable.HashMap[ContainerId, Container]()
  private val containersLaunched = new mutable.HashSet[ContainerId]
  private val containersCompleted = new mutable.HashSet[ContainerId]
  private val containersFailed = new mutable.HashSet[ContainerId]
  private val numFailedContainers = new AtomicInteger

  def containersAllocated(containers: util.List[Container]): Unit = synchronized {
    containers.forEach(container => {
      containerIdToNodeIdMap.update(container.getId, container.getNodeId)
      containersAllocated.update(container.getId, container)
    })
  }

  def containerLaunched(containerId: ContainerId): Unit = synchronized {
    containersLaunched.add(containerId)
  }

  def containerCompleted(containerId: ContainerId): Unit = synchronized {
    containersCompleted.add(containerId)
  }

  def containersCompleted(statuses: util.List[ContainerStatus]): Unit = synchronized {
    statuses.forEach(status => containersCompleted.add(status.getContainerId))
  }

  def forAllLaunchedContainers(f: (ContainerId, NodeId) => Unit): Unit = {
    containerIdToNodeIdMap.foreach {
      case (containerId, nodeId) => f(containerId, nodeId)
    }
  }

  def getProgress: Float = {
    containersCompleted.size
  }

  def areAllContainerRequestsFulfilled(): Boolean = numberOfContainersAllocated == numOfContainersNeeded

  def numberOfContainersAllocated: Int = containersAllocated.size
}

object ContainerStateManager {
  def apply(numOfContainersNeeded: Int) = new ContainerStateManager(numOfContainersNeeded)
}
