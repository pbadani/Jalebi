package com.jalebi.api

case class Jalebi[V, E] private(vertices: Vertices[V], edges: Edges[E]) {

}

object Jalebi {
  def apply[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = Jalebi(vertices, edges)
}
