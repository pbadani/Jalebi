package com.jalebi.driver

import com.jalebi.api.Node
import com.jalebi.proto.jobmanagement.{TaskRequest, TaskType}
import org.apache.commons.lang.StringUtils

object TaskRequestBuilder {

  def searchRequest(jobId: String, vertexId: Node, name: String): TaskRequest = {
    TaskRequest(jobId, StringUtils.EMPTY, TaskType.SEARCH_VERTEX, vertexId.id, name, Nil)
  }

  def loadDatasetRequest(jobId: String, name: String): TaskRequest = {
    TaskRequest(jobId, StringUtils.EMPTY, TaskType.LOAD_DATASET, 0, name, Seq.empty)
  }
}
