package com.jalebi.driver

import com.jalebi.proto.jobmanagement._
import com.jalebi.utils.Logging
import io.grpc.stub.StreamObserver

import scala.concurrent.Future

case class JobManagementServerImpl(jobManager: JobManager) extends JobManagementProtocolGrpc.JobManagementProtocol with Logging {

  override def registerExecutor(request: RegisterExecutorRequest): Future[RegisterExecutorResponse] = {
    LOGGER.info(s"Driver side - Registering executor on server ${request.executorId}")
    jobManager.executorState.markRegistered(request.executorId)
    Future.successful(RegisterExecutorResponse("R"))
  }

  override def startTalk(requestObserver: StreamObserver[TaskResponse]): StreamObserver[TaskRequest] = {
    new StreamObserver[TaskRequest] {
      override def onError(t: Throwable): Unit = {
        LOGGER.info("server error")
      }

      override def onCompleted(): Unit = {
        LOGGER.info("server completed")
      }

      override def onNext(value: TaskRequest): Unit = {
        LOGGER.info(s"on next ${value.jobID}")
      }
    }
  }
}