package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import com.jalebi.context.JalebiConfig._
import com.jalebi.proto.jobmanagement._
import com.jalebi.utils.Logging
import io.grpc.stub.StreamObserver

import scala.concurrent.Future

case class JobManagementServerImpl(jobManager: JobManager, conf: JalebiConfig) extends JobManagementProtocolGrpc.JobManagementProtocol with Logging {

  override def registerExecutor(request: ExecutorRequest): Future[ExecutorResponse] = {
    LOGGER.info(s"Driver side - Registering executor on server ${request.executorId}")
    jobManager.executorState.markRegistered(request.executorId)
    Future.successful(ExecutorResponse(conf.getHeartbeatInterval().toInt, Some(conf.hdfsHostPort.get.toHostPort)))
  }

  override def unregisterExecutor(request: ExecutorRequest): Future[ExecutorResponse] = {
    LOGGER.info(s"Driver side - Unregistering executor on server ${request.executorId}")
    jobManager.executorState.markUnregistered(request.executorId)
    Future.successful(ExecutorResponse(conf.getHeartbeatInterval().toInt))
  }

  override def startTalk(requestObserver: StreamObserver[TaskRequest]): StreamObserver[TaskResponse] = {
    new StreamObserver[TaskResponse] {
      override def onError(t: Throwable): Unit = {
        LOGGER.error("server error")
        LOGGER.error(t.getMessage)
      }

      override def onCompleted(): Unit = {
        LOGGER.info("server completed")
      }

      override def onNext(response: TaskResponse): Unit = {
        val current = System.currentTimeMillis()
        val executorId = response.executorId
        jobManager.executorState.updateLastHeartbeat(executorId, response.executorState, response.datasetState, current)
//        requestObserver.onNext()
        LOGGER.info(s"on next $response")
      }
    }
  }
}