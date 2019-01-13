package com.jalebi.message

import com.jalebi.api.Node
import com.jalebi.hdfs.HostPort

case class StartExecutors(executorIds: Set[String], hostPort: HostPort)

object StopExecutors

case class RegisterExecutor(executorId: String)

case class UnregisterExecutor(executorId: String)

case class RegistrationAcknowledged(hdfs: HostPort)

case class UnregistrationAcknowledged(hdfs: HostPort)

case class LoadedDataset(executorId: String)

case class TaskResult(executorId: String, jobId: String, nodes: Set[Node])

object HeartbeatKey

case class Heartbeat(executorId: String)