package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import com.jalebi.proto.jobmanagement.JobManagementProtocolGrpc
import com.jalebi.utils.Logging
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContext

case class DriverCoordinatorService(jobManager: JobManager, conf: JalebiConfig) extends Runnable with Logging {

  override def run(): Unit = {
    jobManager.shutRunningExecutors()

    val service = JobManagementServerImpl(jobManager, conf)
    val server = ServerBuilder
      .forPort(jobManager.driverHostPort.port)
      .addService(JobManagementProtocolGrpc.bindService(service, ExecutionContext.global))
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


