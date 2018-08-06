package com.jalebi.driver

import com.jalebi.job.JobManager
import com.jalebi.proto.jobmanagement._
import io.grpc.stub.StreamObserver

import scala.concurrent.Future

case class JobManagementServerImpl(jobManager: JobManager) extends JobManagementProtocolGrpc.JobManagementProtocol {
  override def registerExecutor(request: RegisterExecutorRequest): Future[RegisterExecutorResponse] = {

  }

  override def startTalk(responseObserver: StreamObserver[TaskResponse]): StreamObserver[TaskRequest] = ???
}

object JobManagementServerImpl {

}
