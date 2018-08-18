package com.jalebi.executor

import com.jalebi.hdfs.HDFSClient
import com.jalebi.proto.jobmanagement.ExecutorResponse

case class TaskConfig(executorResponse: ExecutorResponse) {

  import com.jalebi.hdfs.HDFSClient._

  val heartbeatInterval: Long = executorResponse.heartbeatInterval

  val hdfsClient: HDFSClient = HDFSClient.withDistributedFileSystem(executorResponse.hostport.map(RichHostPort))
}
