package com.jalebi.common

import com.jalebi.api._
import com.jalebi.proto.jobmanagement.VertexResult

object ResultConverter {

  def convertToVertexResult(vertex: Option[Node]): Seq[VertexResult] = {
    convertToVertices(vertex.toSeq)
  }

  def convertToVertices(vertex: Seq[Node]): Seq[VertexResult] = {
    vertex.map(v => VertexResult(v.getId, v.data))
  }

  def convertFromVertexResult(vertex: Option[VertexResult]): Seq[Node] = {
    convertFromVertices(vertex.toSeq)
  }

  def convertFromVertices(vertex: Seq[VertexResult]): Seq[Node] = {
    vertex.map(v => Node(v.vertexId, v.data))
  }
}
