package com.jalebi.api

case class Jalebi[V, E] private(vertices: Seq[Vertex[V]], edges: Seq[Edge[E]]) {

}

object Jalebi {
  def apply[V, E](vertices: Seq[Vertex[V]], edges: Seq[Edge[E]]): Jalebi[V, E] = Jalebi(vertices, edges)
}
