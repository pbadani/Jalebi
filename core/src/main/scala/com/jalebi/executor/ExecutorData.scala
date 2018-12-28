package com.jalebi.executor

import akka.actor.ActorSelection
import com.jalebi.hdfs.HDFSClient.RichHostPort

trait ExecutorData

object Empty extends ExecutorData

case class ExecutorConfig(masterRef: ActorSelection, hdfs: RichHostPort) extends ExecutorData
