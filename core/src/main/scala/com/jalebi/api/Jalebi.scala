package com.jalebi.api

import scala.collection.mutable

case class Jalebi(name: String, triplets: Set[Triplets]) {

  private val vertices = mutable.HashMap[VertexID, Vertex]()

  triplets.flatMap(_.values).foreach(triplet => {
    val edge = triplet.edge
    require(edge.source == triplet.source.id)
    require(edge.target == triplet.target.id)
    val sourceRef = vertices.getOrElseUpdate(triplet.source.id, triplet.source)
    val targetRef = vertices.getOrElseUpdate(triplet.target.id, triplet.target)
    sourceRef.addRelation(edge)
    targetRef.addRelation(edge)
    edge.setSourceRef(sourceRef)
    edge.setTargetRef(targetRef)
  })

  //    def searchBreadthFirst(verticesToSearch: MatchCriteria[V], edgesToTraverse: MatchCriteria[E]): Unit = {
  //      context.jobManager.breadthFirst(verticesToSearch, edgesToTraverse)
  //    }
  //
  //  def searchDepthFirst(verticesToSearch: MatchCriteria[V], edgesToTraverse: MatchCriteria[E]): Unit = {
  //
  //  }
  override def toString: String = {
    triplets.flatMap(_.values).map(triplet =>
      s"${Set(triplet.source.id, "--", triplet.target.id).mkString("\t\t")}")
      .mkString(sys.props("line.separator"))
  }
}