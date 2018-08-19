package com.jalebi.api

case class Triplet(source: Vertex, edge: Edge, target: Vertex)

object Triplet {
  def apply(source: Vertex, edge: Edge, target: Vertex): Triplet = new Triplet(source, edge, target)
}
