package com.jalebi.common

import com.jalebi.api.Vertex
import com.jalebi.proto.jobmanagement.VertexResult

object ResultConverter {

  def convertToVertexResult(vertex: Option[Vertex]): Seq[VertexResult] = {
    vertex.map(v => VertexResult(v.getId, v.data)).toSeq
  }

  def convertToVertices(vertex: Seq[Vertex]): Seq[VertexResult] = {
    vertex.map(v => VertexResult(v.getId, v.data))
  }
}
