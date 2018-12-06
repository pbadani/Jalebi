package com.jalebi.driver

import com.jalebi.common.Logging
import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement._
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver

import scala.concurrent.{ExecutionContext, Future}

case class Driver(jobManager: JobManager, conf: JalebiConfig) extends Runnable with Logging {

  private val serverHandler = new JobManagementProtocolGrpc.JobManagementProtocol with Logging {
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
          LOGGER.error(s"on error - Driver ${t.getMessage} ${t.getCause}.")
        }

        override def onCompleted(): Unit = {
          LOGGER.info("on completed - Driver.")
        }

        override def onNext(response: TaskResponse): Unit = {
          val executorId = response.executorId
          jobManager.executorState.updateLastHeartbeat(executorId, response.executorState, response.datasetState, System.currentTimeMillis())
          jobManager.resultAggregator.saveTaskResult(response)
          jobManager.executorState.consumeNextTask(executorId).foreach(request => {
            LOGGER.info(s"Issuing new task $request to executor $executorId.")
            requestObserver.onNext(request)
          })
          if (response.jobId.isEmpty) {
            LOGGER.debug(s"on next $response.")
          } else {
            LOGGER.info(s"on next $response.")
          }
        }
      }
    }
  }

  private val service = JobManagementProtocolGrpc.bindService(serverHandler, ExecutionContext.global)
  private val server = ServerBuilder.forPort(jobManager.driverHostPort.port.toInt).addService(service).build()

  override def run(): Unit = {
    server.start()
    LOGGER.info(s"Started Listening on port: ${jobManager.driverHostPort.port}")
    try {
      server.awaitTermination()
    } catch {
      case _: InterruptedException =>
        LOGGER.info("Shutting down the server.")
        server.shutdown()
    }
  }
}

object Driver {
  def apply(jobManager: JobManager, conf: JalebiConfig): Thread = new Thread(new Driver(jobManager, conf))
}
