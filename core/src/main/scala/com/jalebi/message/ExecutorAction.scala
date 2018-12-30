package com.jalebi.message

import com.jalebi.api.Node

trait ExecutorAction

case class LoadDatasetTask(jobId: String, name: String, parts: Set[String]) extends ExecutorAction

case class FindNodeTask(jobId: String, node: Long) extends ExecutorAction

case class TaskResult(jobId: String, nodes: Set[Node]) extends ExecutorAction

object ShutExecutors extends ExecutorAction