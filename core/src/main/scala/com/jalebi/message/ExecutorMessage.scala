package com.jalebi.message

import com.jalebi.hdfs.HDFSClient.RichHostPort

case class StartExecutors(executorIds: Set[String], hostPort: RichHostPort)

case class StopExecutors()

case class RegisterExecutor(executorId: String)

case class UnregisterExecutor(executorId: String)

case class RegistrationAcknowledged(hdfs: RichHostPort)

case class UnregistrationAcknowledged(hdfs: RichHostPort)

case class LoadedDataset(executorId: String)

object HeartbeatKey

case class Heartbeat(executorId: String)