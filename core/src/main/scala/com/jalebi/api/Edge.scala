package com.jalebi.api

case class Edge(source: Node, target: Node, data: Map[String, String], isDirected: Boolean) {

  private var sourceRef: Option[Node] = None

  private var targetRef: Option[Node] = None

  def isSource(id: Long): Boolean = source.id == id

  def isTarget(id: Long): Boolean = target.id == id

  def setSourceRef(node: Node): Unit = {
    require(sourceRef.isEmpty, s"Source already referring to $sourceRef.")
    require(node.id == source.id, s"Source id $source does not match this node id ${node.id}")
    sourceRef = Some(node)
  }

  def setTargetRef(node: Node): Unit = {
    require(targetRef.isEmpty, s"Target already referring to $targetRef.")
    require(node.id == target.id, s"Target id $target does not match this node id ${node.id}")
    targetRef = Some(node)
  }
}

object Edge {
  def apply(source: Node, target: Node, data: Map[String, String], isDirected: Boolean): Edge = new Edge(source, target, data, isDirected)
}