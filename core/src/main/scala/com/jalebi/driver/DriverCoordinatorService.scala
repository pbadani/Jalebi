package com.jalebi.driver

import com.jalebi.job.JobManager
import com.jalebi.proto.jobmanagement.JobManagementProtocolGrpc
import com.jalebi.utils.Logging
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContext

case class DriverCoordinatorService(jobManager: JobManager) extends Logging {

  jobManager.shutRunningExecutors()

  def start(): Unit = {
    val server = ServerBuilder
      .forPort(jobManager.driverHostPort.port.toInt)
      .addService(JobManagementProtocolGrpc.bindService(new JobManagementServerImpl(jobManager), ExecutionContext.global))
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