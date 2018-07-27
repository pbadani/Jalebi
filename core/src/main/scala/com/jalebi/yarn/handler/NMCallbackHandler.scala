package com.jalebi.yarn.handler

import java.nio.ByteBuffer
import java.util

import org.apache.hadoop.yarn.api.records.{ContainerId, ContainerStatus, Resource}
import org.apache.hadoop.yarn.client.api.async.NMClientAsync.AbstractCallbackHandler

class NMCallbackHandler extends AbstractCallbackHandler{
  override def onGetContainerStatusError(containerId: ContainerId, t: Throwable): Unit = ???

  override def onContainerStatusReceived(containerId: ContainerId, containerStatus: ContainerStatus): Unit = ???

  override def onContainerResourceIncreased(containerId: ContainerId, resource: Resource): Unit = ???

  override def onStopContainerError(containerId: ContainerId, t: Throwable): Unit = ???

  override def onContainerStopped(containerId: ContainerId): Unit = ???

  override def onIncreaseContainerResourceError(containerId: ContainerId, t: Throwable): Unit = ???

  override def onStartContainerError(containerId: ContainerId, t: Throwable): Unit = ???

  override def onUpdateContainerResourceError(containerId: ContainerId, t: Throwable): Unit = ???

  override def onContainerStarted(containerId: ContainerId, allServiceResponse: util.Map[String, ByteBuffer]): Unit = ???

  override def onContainerResourceUpdated(containerId: ContainerId, resource: Resource): Unit = ???
}

object NMCallbackHandler {
  def apply(): NMCallbackHandler = new NMCallbackHandler()
}
