package com.jalebi.api

case class Triplet[V, E](source: Vertex[V], edge: Edge[E], target: Vertex[V]) {
  require(source.id == edge.source, s"Source id ${source.id} doesn't match with edge source ${edge.source}")
  require(target.id == edge.target, s"Target id ${target.id} doesn't match with edge target ${edge.target}")
}

object Triplet {
  def apply[V, E](source: Vertex[V], edge: Edge[E], target: Vertex[V]): Triplet[V, E] = new Triplet(source, edge, target)
}
