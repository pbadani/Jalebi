package com.jalebi.context

import com.jalebi.api.{Edge, Jalebi, Vertex}

case class JalebiContext(conf: JalebiConfig) {

  def load[V, E](vertices: Vertex[V], edges: Edge[E]): Jalebi[V, E] = {

  }
}
