package com.jalebi.executor.local

import com.jalebi.executor.{TaskConfig, TaskManager}
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.proto.jobmanagement._
import com.jalebi.utils.Logging
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

case class LocalRunnable(taskManager: TaskManager, driverHostPort: RichHostPort) extends Runnable with Logging {

  //  private val jobManagementClient = new JobManagementClientImpl()

  override def run(): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress(driverHostPort.host, driverHostPort.port)
      .usePlaintext().build()

    val stub = JobManagementProtocolGrpc.stub(channel)

    import scala.concurrent.ExecutionContext.Implicits.global
    LOGGER.info(s"Registering executor ${taskManager.executorId}.")
    stub.registerExecutor(ExecutorRequest(taskManager.executorId)).onComplete(r => {
      taskManager.markRegistered(TaskConfig(r.get))
    })
    LOGGER.info(s"Registered executor ${taskManager.executorId}.")

    val resp: StreamObserver[TaskResponse] = stub.startTalk(
      new StreamObserver[TaskRequest] {
        override def onError(t: Throwable): Unit = {
          LOGGER.info(s"On Error ${taskManager.executorId}")
        }

        override def onCompleted(): Unit = {
          LOGGER.info(s"On Complete ${taskManager.executorId}")
        }

        override def onNext(taskRequest: TaskRequest): Unit = {
          taskManager.execute(taskRequest)
          LOGGER.info(s"On Next ${taskManager.executorId}")
        }
      })

    while (taskManager.keepRunning) {
      resp.onNext(TaskResponse("", taskManager.executorId, taskManager.currentState))
      Thread.sleep(taskManager.heartbeatInterval)
    }
  }

  def terminate(): Unit = {
    taskManager.keepRunning
  }
}
