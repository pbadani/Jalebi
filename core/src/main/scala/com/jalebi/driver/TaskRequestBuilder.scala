package com.jalebi.driver

import com.jalebi.api.VertexID
import com.jalebi.proto.jobmanagement.{TaskRequest, TaskType}

object TaskRequestBuilder {

  def searchRequest(jobId: String, vertexId: VertexID, name: String): TaskRequest = {
    TaskRequest(jobId, TaskType.SEARCH_VERTEX, vertexId.id, name, Nil)
  }

  def loadDatasetRequest(jobId: String, name: String, parts: Seq[String]): TaskRequest = {
    TaskRequest(jobId, TaskType.LOAD_DATASET, 0, name, parts)
  }
}
