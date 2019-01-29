package com.jalebi.message

import com.jalebi.api.Node
import com.jalebi.hdfs.HostPort
import org.apache.hadoop.yarn.api.records.Container

case class StartExecutors(executorIds: Set[String])

object StopExecutors

case class RegisterExecutor(executorId: String)

case class UnregisterExecutor(executorId: String)

case class RegistrationAcknowledged(hdfs: HostPort)

case class UnregistrationAcknowledged(hdfs: HostPort)

case class LoadedDataset(executorId: String)

case class FullResult(executorId: String, jobId: String, nodes: Set[Node])

object HeartbeatKey

case class Heartbeat(executorId: String)

case class LaunchContainer(executorId: String, allocatedContainer: Container)

case class RemoveContainer(allocatedContainer: Container)

case class ContainerRequested(executorId: String, requestId: Long)

case class ContainerAllocated(container: Container)