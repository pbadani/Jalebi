package com.jalebi.executor

import akka.actor.ActorSelection
import com.jalebi.api.Jalebi
import com.jalebi.hdfs.HostPort

trait ExecutorData

object Empty extends ExecutorData

case class RegisteredExecutorState(monitorRef: ActorSelection, hdfs: HostPort) extends ExecutorData

case class LoadedExecutorState(monitorRef: ActorSelection, hdfs: HostPort, jalebi: Jalebi) extends ExecutorData
