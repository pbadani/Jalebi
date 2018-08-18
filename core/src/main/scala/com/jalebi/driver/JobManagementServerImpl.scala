package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement._
import com.jalebi.utils.Logging
import io.grpc.stub.StreamObserver

import scala.concurrent.Future

case class JobManagementServerImpl(jobManager: JobManager, conf: JalebiConfig) extends JobManagementProtocolGrpc.JobManagementProtocol with Logging {

  import com.jalebi.context.JalebiConfig._

  override def registerExecutor(request: ExecutorRequest): Future[ExecutorResponse] = {
    LOGGER.info(s"Driver side - Registering executor on server ${request.executorId}")
    jobManager.executorState.markRegistered(request.executorId)
    Future.successful(ExecutorResponse(conf.getHeartbeatInterval().toInt))
  }

  override def unregisterExecutor(request: ExecutorRequest): Future[ExecutorResponse] = {
    LOGGER.info(s"Driver side - Unregistering executor on server ${request.executorId}")
    jobManager.executorState.markUnregistered(request.executorId)
    Future.successful(ExecutorResponse(conf.getHeartbeatInterval().toInt))
  }

  override def startTalk(requestObserver: StreamObserver[TaskRequest]): StreamObserver[TaskResponse] = {
    new StreamObserver[TaskResponse] {
      override def onError(t: Throwable): Unit = {
        LOGGER.info("server error")
      }

      override def onCompleted(): Unit = {
        LOGGER.info("server completed")
      }

      override def onNext(value: TaskResponse): Unit = {
        LOGGER.info(s"on next ${value.executorId}")
      }
    }
  }

}