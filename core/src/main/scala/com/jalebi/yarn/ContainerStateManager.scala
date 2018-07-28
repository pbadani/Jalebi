package com.jalebi.yarn

import java.util
import java.util.concurrent.atomic.AtomicInteger

import org.apache.hadoop.yarn.api.records.{Container, ContainerId, ContainerStatus}

import scala.collection.mutable

case class ContainerStateManager(numOfContainersNeeded: Int) {
  private val containersAllocated = new mutable.HashMap[ContainerId, Container]()
  private val containersLaunched = new mutable.HashSet[ContainerId]
  private val containersCompleted = new mutable.HashSet[ContainerId]
  private val containersFailed = new mutable.HashSet[ContainerId]
  private val numFailedContainers = new AtomicInteger

  def containersAllocated(containers: util.List[Container]): Unit = synchronized {
    containers.forEach(container => containersAllocated.update(container.getId, container))
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

  def areAllContainerRequestsFulfilled(): Boolean = numberOfContainersAllocated == numOfContainersNeeded

  def numberOfContainersAllocated: Int = containersAllocated.size
}

object ContainerStateManager {
  def apply(numOfContainersNeeded: Int) = new ContainerStateManager(numOfContainersNeeded)
}
