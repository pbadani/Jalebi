package com.jalebi.message

trait ExecutorAction

case class LoadDatasetTask(jobId: String, name: String, parts: Set[String]) extends ExecutorAction