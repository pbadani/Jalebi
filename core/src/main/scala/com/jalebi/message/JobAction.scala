package com.jalebi.message

import org.apache.commons.lang.StringUtils

abstract class JobAction(val jobId: String = StringUtils.EMPTY) extends Serializable

case class LoadDataset(name: String, override val jobId: String = StringUtils.EMPTY, parts: Set[String] = Set.empty) extends JobAction

case class FindNode(nodeId: Long, override val jobId: String = StringUtils.EMPTY) extends JobAction

object ShutExecutors extends JobAction

object InitializeExecutors extends JobAction

object Shutdown extends JobAction
