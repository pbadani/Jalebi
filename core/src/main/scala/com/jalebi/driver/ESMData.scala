package com.jalebi.driver

import com.jalebi.proto.jobmanagement.{DatasetState, TaskRequest}
import org.apache.hadoop.yarn.api.records.Container

sealed trait ESMData

object Nothing extends ESMData

case class ExecutorData(executorIdToState: Map[String, StateValue]) extends ESMData

case class StateValue(parts: Set[String],
                      executorState: ExecutorState,
                      datasetState: DatasetState,
                      container: Option[Container],
                      nextAction: Option[TaskRequest])