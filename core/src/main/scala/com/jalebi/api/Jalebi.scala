package com.jalebi.api

case class Jalebi(name: String, triplets: Set[Triplets]) {

  private val vertices = new VertexMap()

  triplets.flatMap(_.values).foreach(triplet => {
    val edge = triplet.edge
    require(edge.source == triplet.source.vertexId)
    require(edge.target == triplet.target.vertexId)
    val sourceRef = vertices.add(triplet.source.vertexId, triplet.source)
    val targetRef = vertices.add(triplet.target.vertexId, triplet.target)
    sourceRef.addRelation(edge)
    targetRef.addRelation(edge)
    edge.setSourceRef(sourceRef)
    edge.setTargetRef(targetRef)
  })

  def searchVertex(vertexId: VertexID): Option[Vertex] = {
    vertices.get(vertexId)
  }

  //
  //  def searchDepthFirst(verticesToSearch: MatchCriteria[V], edgesToTraverse: MatchCriteria[E]): Unit = {
  //
  //  }
  override def toString: String = {
    triplets.flatMap(_.values).map(triplet =>
      s"${Set(triplet.source.vertexId, "--", triplet.target.vertexId).mkString("\t\t")}")
      .mkString(sys.props("line.separator"))
  }
}