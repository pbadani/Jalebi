package com.jalebi.api

import com.jalebi.api.search.MatchCriteria
import com.jalebi.context.JalebiContext

abstract class Jalebi[V, E] private(context: JalebiContext, vertices: Vertices[V], edges: Edges[E]) {

  def searchBreadthFirst(verticesToSearch: MatchCriteria[V], edgesToTraverse: MatchCriteria[E]): Unit

  def searchDepthFirst(verticesToSearch: MatchCriteria[V], edgesToTraverse: MatchCriteria[E]): Unit

}

object Jalebi {
  def createLocal[V, E](context: JalebiContext, vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = Jalebi(vertices, edges)
}
