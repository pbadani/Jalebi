package com.jalebi.yarn.handler

import java.util

import org.apache.hadoop.yarn.api.records.{Container, ContainerStatus, NodeReport, UpdatedContainer}
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.AbstractCallbackHandler

class AMRMCallbackHandler extends AbstractCallbackHandler {


  override def onContainersUpdated(containers: util.List[UpdatedContainer]): Unit = ???

  override def onError(e: Throwable): Unit = ???

  override def onShutdownRequest(): Unit = ???

  override def onContainersCompleted(statuses: util.List[ContainerStatus]): Unit = ???

  override def getProgress: Float = ???

  override def onNodesUpdated(updatedNodes: util.List[NodeReport]): Unit = ???

  override def onContainersAllocated(containers: util.List[Container]): Unit = {

  }
}

object AMRMCallbackHandler {
  def apply(): AMRMCallbackHandler = new AMRMCallbackHandler()
}
