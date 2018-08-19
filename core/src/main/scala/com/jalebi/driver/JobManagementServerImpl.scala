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
        LOGGER.error(s"on error - Driver ${t.getMessage} ${t.getCause}")
      }

      override def onCompleted(): Unit = {
        LOGGER.info("on completed - Driver")
      }

      override def onNext(response: TaskResponse): Unit = {
        val executorId = response.executorId
        val executorState = jobManager.executorState
        executorState.updateLastHeartbeat(executorId, response.executorState, response.datasetState, System.currentTimeMillis())
        val nextTask = executorState.consumeNextTask(executorId)
        if (nextTask.isDefined) {
          LOGGER.info(s"Issuing new task $nextTask to executor $executorId")
          requestObserver.onNext(nextTask.get)
        }
        LOGGER.info(s"on next $response")
      }
    }
  }
}