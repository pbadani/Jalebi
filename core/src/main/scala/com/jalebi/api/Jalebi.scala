package com.jalebi.api

abstract class Jalebi[V, E] private(vertices: Vertices[V], edges: Edges[E]) {

  def searchBreadthFirst(verticesToSearch: Option[V => Boolean], edgesToTraverse: Option[E => Boolean]): Unit
  def searchDepthFirst(verticesToSearch: Option[V => Boolean], edgesToTraverse: Option[E => Boolean]): Unit


}

object Jalebi {
  def createLocal[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = Jalebi(vertices, edges)
}
