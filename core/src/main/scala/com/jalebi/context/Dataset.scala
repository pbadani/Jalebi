package com.jalebi.context

import com.jalebi.api.{Vertex, VertexID}
import com.jalebi.driver.JobManager

case class Dataset(name: String, jobManager: JobManager) {

  def findVertex(vertexId: VertexID): Set[Vertex] = {
    jobManager.findVertex(vertexId, name)
  }

  def breadthFirst(startFrom: VertexID) = {

  }

}
