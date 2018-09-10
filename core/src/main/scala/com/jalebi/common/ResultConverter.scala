package com.jalebi.common

import com.jalebi.api.{Vertex, VertexID}
import com.jalebi.proto.jobmanagement.VertexResult

object ResultConverter {

  def convertToVertexResult(vertex: Option[Vertex]): Seq[VertexResult] = {
    convertToVertices(vertex.toSeq)
  }

  def convertToVertices(vertex: Seq[Vertex]): Seq[VertexResult] = {
    vertex.map(v => VertexResult(v.getId, v.data))
  }

  def convertFromVertexResult(vertex: Option[VertexResult]): Seq[Vertex] = {
    convertFromVertices(vertex.toSeq)
  }

  def convertFromVertices(vertex: Seq[VertexResult]): Seq[Vertex] = {
    vertex.map(v => Vertex(VertexID(v.vertexId), v.data))
  }
}
