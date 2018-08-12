package com.jalebi.executor.standalone

import com.jalebi.executor.JobManagementClientImpl
import com.jalebi.hdfs.HostPort
import com.jalebi.proto.jobmanagement.{JobManagementProtocolGrpc, RegisterExecutorRequest, TaskResponse}
import com.jalebi.utils.Logging
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

case class LocalRunnable(threadId: String, driverHostPort: HostPort) extends Runnable with Logging {

  private var running = true
  private val jobManagementClient = JobManagementClientImpl()

  override def run(): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress(driverHostPort.host, driverHostPort.port.toInt)
      .usePlaintext().build()

    val stub = JobManagementProtocolGrpc.stub(channel)
    LOGGER.info(s"Registering executor $threadId.")

    val response = stub.registerExecutor(RegisterExecutorRequest(threadId))
    LOGGER.info(s"Registered executor $threadId.")

    stub.startTalk(new StreamObserver[TaskResponse] {
      override def onError(t: Throwable): Unit = {
        LOGGER.info(s"On Error $threadId")
      }

      override def onCompleted(): Unit = {
        LOGGER.info(s"On Complete $threadId")
      }

      override def onNext(value: TaskResponse): Unit = {
        LOGGER.info(s"On Next $threadId")
      }
    })
  }

  def terminate(): Unit = {
    running = false
  }
}
