package com.jalebi.executor.local

import com.jalebi.executor.TaskManager
import com.jalebi.hdfs.HostPort
import com.jalebi.proto.jobmanagement.{ExecutorRequest, JobManagementProtocolGrpc, TaskRequest, TaskResponse}
import com.jalebi.utils.Logging
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver

case class LocalRunnable(taskManager: TaskManager, driverHostPort: HostPort) extends Runnable with Logging {

  private var running = true
  private var heartbeatInterval: Long = 5

  //  private val jobManagementClient = new JobManagementClientImpl()

  override def run(): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress(driverHostPort.host, driverHostPort.port.toInt)
      .usePlaintext().build()

    val stub = JobManagementProtocolGrpc.stub(channel)

    import scala.concurrent.ExecutionContext.Implicits.global
    LOGGER.info(s"Registering executor $taskManager.executorId.")
    stub.registerExecutor(ExecutorRequest(taskManager.executorId)).onComplete(r => {
      taskManager.markRegistered()
      heartbeatInterval = r.get.heartbeatInterval
    })
    LOGGER.info(s"Registered executor $taskManager.executorId.")

    val resp: StreamObserver[TaskResponse] = stub.startTalk(
      new StreamObserver[TaskRequest] {
        override def onError(t: Throwable): Unit = {
          LOGGER.info(s"On Error $taskManager.executorId")
        }

        override def onCompleted(): Unit = {
          LOGGER.info(s"On Complete $taskManager.executorId")
        }

        override def onNext(value: TaskRequest): Unit = {

          LOGGER.info(s"On Next $taskManager.executorId")
        }
      })

    while (true) {
      resp.onNext(TaskResponse("", taskManager.executorId, taskManager.currentState))
      Thread.sleep(heartbeatInterval)
    }
  }

  def terminate(): Unit = {
    running = false
  }
}
