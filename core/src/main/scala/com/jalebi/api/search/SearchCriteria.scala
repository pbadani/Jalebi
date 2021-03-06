package com.jalebi.api.search

import com.jalebi.api.{Edge, Node}

case class SearchCriteria[V <: Node, E <: Edge](verticesToSearch: MatchCriteria[V] = MatchAll, edgesToTraverse: MatchCriteria[E] = MatchAll, limit: LimitCriteria) {

  def vertexMatches(vertex: V): Boolean = verticesToSearch.matches(vertex)

  def edgeMatches(edge: E): Boolean = edgesToTraverse.matches(edge)

  def limitReached(resultSetCount: Long): Boolean = limit.isReached(resultSetCount)

}
