package com.jalebi.api

case class Triplet[V, E](source: Vertex[V], edge: Edge[E], target: Vertex[V]) {
  source.addRelation(edge)
  target.addRelation(edge)
  //  edge.setSourceRef(source)
  //  edge.setTargetRef(target)
}

object Triplet {
  def apply[V, E](source: Vertex[V], edge: Edge[E], target: Vertex[V]): Triplet[V, E] = new Triplet(source, edge, target)
}
