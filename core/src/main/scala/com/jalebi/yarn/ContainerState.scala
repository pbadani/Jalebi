package com.jalebi.yarn

import java.util
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import org.apache.hadoop.yarn.api.records.ContainerId

//mutable state encapsulated here so that it doesn't spill over in the code.
case class ContainerState(numOfContainersNeeded: Int) {
  var numAllocatedContainers = new AtomicInteger
  var numCompletedContainers = new AtomicInteger
  var numFailedContainers = new AtomicInteger

  val launchedContainers: util.Set[ContainerId] = Collections.newSetFromMap(ConcurrentHashMap[ContainerId, Boolean])

  def incrementAllocated() = synchronized {
    numAllocatedContainers.incrementAndGet()
  }

  def getNumContainersToRequest(): Int = {
    numOfContainersNeeded - numAllocatedContainers.get()
  }

}

object ContainerState {
  def apply(numOfContainersNeeded: Int) = new ContainerState(numOfContainersNeeded)
}
