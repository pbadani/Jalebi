package com.jalebi.api

case class Edge[T](source: VertexID, target: VertexID, data: T, isDirected: Boolean) {

  private var sourceRef: Option[Vertex[_]] = None

  private var targetRef: Option[Vertex[_]] = None

  def isSource(id: VertexID): Boolean = source == id

  def isTarget(id: VertexID): Boolean = target == id

  def setSourceRef(vertex: Vertex[_]): Unit = {
    require(sourceRef.isEmpty, s"Source already referring to $sourceRef.")
    require(vertex.id == source, s"Source id $source does not match this vertex id ${vertex.id}")
    sourceRef = Some(vertex)
  }

  def setTargetRef(vertex: Vertex[_]): Unit = {
    require(targetRef.isEmpty, s"Target already referring to $targetRef.")
    require(vertex.id == target, s"Target id $target does not match this vertex id ${vertex.id}")
    targetRef = Some(vertex)
  }
}

object Edge {
  def apply[T](source: VertexID, target: VertexID, data: T, isDirected: Boolean): Edge[T] = new Edge[T](source, target, data, isDirected)
}