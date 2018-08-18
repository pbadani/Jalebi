package com.jalebi.executor

import com.jalebi.proto.jobmanagement._

class JobManagementClientImpl extends JobManagementProtocolGrpc.JobManagementProtocolBlockingClient {

  override def registerExecutor(request: RegisterExecutorRequest): RegisterExecutorResponse = {
    null
  }

  override def heartbeat(request: HeartbeatRequest): HeartbeatResponse = {
    null
  }
}
