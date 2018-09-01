package com.jalebi.executor

import com.jalebi.common.Logging
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.proto.jobmanagement._
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

import scala.concurrent.ExecutionContext.Implicits.global

case class Executor(taskManager: TaskManager, driverHostPort: RichHostPort) extends Runnable with Logging {

  override def run(): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress(driverHostPort.host, driverHostPort.port.toInt)
      .usePlaintext().build()

    val stub = JobManagementProtocolGrpc.stub(channel)
    stub.registerExecutor(ExecutorRequest(taskManager.executorId)).onComplete(r => {
      taskManager.markRegistered(TaskConfig(r.get))
    })
    LOGGER.info(s"Registered executor ${taskManager.executorId}.")

    val resp: StreamObserver[TaskResponse] = stub.startTalk(
      new StreamObserver[TaskRequest] {
        override def onError(t: Throwable): Unit = {
          LOGGER.error(s"on error - Executor ${taskManager.executorId} ${t.getMessage} ${t.getCause} ${t.getStackTrace}")
        }

        override def onCompleted(): Unit = {
          LOGGER.info(s"on Complete - Executor ${taskManager.executorId}")
        }

        override def onNext(taskRequest: TaskRequest): Unit = {
          LOGGER.info(s"on next - Executor ${taskManager.executorId} $taskRequest")
          taskManager.execute(taskRequest)
        }
      })

    while (taskManager.keepRunning) {
      resp.onNext(taskManager.propagateInHeartbeat.get)
      Thread.sleep(taskManager.heartbeatInterval * 1000)
    }
  }

  def terminate(): Unit = {
    taskManager.keepRunning
  }
}

object Executor {

  def main(args: Array[String]): Unit = {
    val executorArgs = ExecutorArgs(args)
    val executor = Executor(TaskManager(executorArgs.getExecutorId), executorArgs.getDriverHostPort)
    executor.run()
  }
}