package com.jalebi.api

case class Edge(source: VertexID, target: VertexID, data: Map[String, String], isDirected: Boolean) {

  private var sourceRef: Option[Vertex] = None

  private var targetRef: Option[Vertex] = None

  def isSource(id: VertexID): Boolean = source == id

  def isTarget(id: VertexID): Boolean = target == id

  def setSourceRef(vertex: Vertex): Unit = {
    require(sourceRef.isEmpty, s"Source already referring to $sourceRef.")
    require(vertex.id == source, s"Source id $source does not match this vertex id ${vertex.id}")
    sourceRef = Some(vertex)
  }

  def setTargetRef(vertex: Vertex): Unit = {
    require(targetRef.isEmpty, s"Target already referring to $targetRef.")
    require(vertex.id == target, s"Target id $target does not match this vertex id ${vertex.id}")
    targetRef = Some(vertex)
  }
}

object Edge {
  def apply(source: VertexID, target: VertexID, data: Map[String, String], isDirected: Boolean): Edge = new Edge(source, target, data, isDirected)
}