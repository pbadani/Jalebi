package com.jalebi.driver

import com.jalebi.api.VertexID
import com.jalebi.proto.jobmanagement.{TaskRequest, TaskType}

object TaskRequestBuilder {

  def searchRequest(vertexId: VertexID, name: String): TaskRequest = {
    TaskRequest(TaskType.SEARCH_VERTEX, vertexId.id, name, Nil)
  }

  def loadDatasetRequest(name: String, parts: Seq[String]): TaskRequest = {
    TaskRequest(TaskType.LOAD_DATASET, 0, name, parts)
  }
}
