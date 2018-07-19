package com.jalebi.context

import com.jalebi.api._

case class JalebiContext private(conf: JalebiConfig) {

  @throws[IllegalArgumentException]
  private def validate[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = {
    //    val vertexID
    null
  }

  @throws[IllegalArgumentException]
  def load[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = {
    validate(vertices, edges)
    Jalebi.apply(vertices, edges)
  }
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = JalebiContext(conf)
}
