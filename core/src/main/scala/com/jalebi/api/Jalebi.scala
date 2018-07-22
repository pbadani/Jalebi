package com.jalebi.api

import com.jalebi.api.search.Criteria

abstract class Jalebi[V, E] private(vertices: Vertices[V], edges: Edges[E]) {

  def searchBreadthFirst(verticesToSearch: Criteria[V], edgesToTraverse: Criteria[E]): Unit

  def searchDepthFirst(verticesToSearch: Criteria[V], edgesToTraverse: Criteria[E]): Unit

}

object Jalebi {
  def createLocal[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = Jalebi(vertices, edges)
}
