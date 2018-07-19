package com.jalebi.api

abstract case class Edge[T](source: VertexID, target: VertexID, data: T) extends EdgeDirection with EdgeWeight {

  self: EdgeDirection with EdgeWeight =>

  def isSource(id: VertexID): Boolean = source == id

  def isTarget(id: VertexID): Boolean = target == id
}

//object Edge {
//  def apply[T](source: VertexID, target: VertexID, data: T): Edge[T] = new Edge[T](source, target, data)
//}

object Test {
  new Edge[Int](null, null, 1) with Undirected with Weighted {
    override def weight: Long = 1
  }
}