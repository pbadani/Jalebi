package com.jalebi.api

case class Vertices[V](set: Set[Vertex[V]]) {

  def connect[E](edges: Edge[E]): Jalebi[V, E] = ???
}
