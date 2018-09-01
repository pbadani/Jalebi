package com.jalebi.executor

import com.jalebi.hdfs.HDFSClient
import com.jalebi.proto.jobmanagement.ExecutorResponse
import org.apache.hadoop.yarn.conf.YarnConfiguration

case class TaskConfig(executorResponse: ExecutorResponse) {

  import com.jalebi.hdfs.HDFSClient._

  val heartbeatInterval: Long = executorResponse.heartbeatInterval

  val hdfsClient: HDFSClient = HDFSClient.withDistributedFileSystem(executorResponse.hostport.map(RichHostPort), new YarnConfiguration())
}
