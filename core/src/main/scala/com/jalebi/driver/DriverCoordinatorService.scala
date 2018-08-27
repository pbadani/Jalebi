package com.jalebi.driver

import com.jalebi.common.Logging
import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement.JobManagementProtocolGrpc
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContext

case class DriverCoordinatorService(jobManager: JobManager, conf: JalebiConfig) extends Runnable with Logging {

  override def run(): Unit = {
    jobManager.shutRunningExecutors()

    val serverHandler = JobManagementServerImpl(jobManager, conf)
    val service = JobManagementProtocolGrpc.bindService(serverHandler, ExecutionContext.global)
    val server = ServerBuilder
      .forPort(jobManager.driverHostPort.port.toInt)
      .addService(service)
      .build()
      .start()

    LOGGER.info(s"Started Listening on port: ${jobManager.driverHostPort.port}")
    server.awaitTermination()
    sys.addShutdownHook({
      LOGGER.info("Shutting down the server.")
      server.shutdown()
    })
  }
}

object DriverCoordinatorService {
  def apply(jobManager: JobManager, conf: JalebiConfig): Thread = new Thread(new DriverCoordinatorService(jobManager, conf))
}


