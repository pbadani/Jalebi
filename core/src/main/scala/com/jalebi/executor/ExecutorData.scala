package com.jalebi.executor

import akka.actor.ActorSelection
import com.jalebi.api.Jalebi
import com.jalebi.hdfs.HDFSClient.RichHostPort

trait ExecutorData

object Empty extends ExecutorData

case class RegisteredExecutorState(monitorRef: ActorSelection, hdfs: RichHostPort) extends ExecutorData

case class LoadedExecutorState(monitorRef: ActorSelection, hdfs: RichHostPort, jalebi: Jalebi) extends ExecutorData
