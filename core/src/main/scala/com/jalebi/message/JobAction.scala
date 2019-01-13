package com.jalebi.message

import org.apache.commons.lang.StringUtils

trait JobAction extends Serializable

case class LoadDataset(name: String, jobId: String = StringUtils.EMPTY, parts: Set[String] = Set.empty) extends JobAction

case class FindNode(nodeId: Long, jobId: String = StringUtils.EMPTY) extends JobAction

object ShutExecutors extends JobAction

object InitializeExecutors extends JobAction

object Shutdown extends JobAction
