package com.jalebi.executor

import com.jalebi.api.Vertex
import com.jalebi.proto.jobmanagement.VertexResult

object ResultConverter {

  def convertVertex(vertex: Option[Vertex]): Seq[VertexResult] = {
    vertex.map(v => VertexResult(v.getId, v.data)).toSeq
  }

  def convertVertices(vertex: Seq[Vertex]): Seq[VertexResult] = {
    vertex.map(v => VertexResult(v.getId, v.data))
  }
}
