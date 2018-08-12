package com.jalebi.executor

import com.jalebi.proto.jobmanagement.{JobManagementProtocolGrpc, RegisterExecutorRequest, RegisterExecutorResponse}

case class JobManagementClientImpl() extends JobManagementProtocolGrpc.JobManagementProtocolBlockingClient {

  override def registerExecutor(request: RegisterExecutorRequest): RegisterExecutorResponse = {
    null
  }

}
