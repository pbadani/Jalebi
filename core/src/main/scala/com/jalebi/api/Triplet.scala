package com.jalebi.api

case class Triplet(source: Vertex, edge: Edge, target: Vertex) {
  source.addRelation(edge)
  target.addRelation(edge)
  //  edge.setSourceRef(source)
  //  edge.setTargetRef(target)
}

object Triplet {
  def apply(source: Vertex, edge: Edge, target: Vertex): Triplet = new Triplet(source, edge, target)
}
