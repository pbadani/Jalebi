package com.jalebi.message

trait Job

case class LoadDataset(name: String) extends Job

case class FindNode(nodeId: Long) extends Job

object InitializeExecutors

object Shutdown
