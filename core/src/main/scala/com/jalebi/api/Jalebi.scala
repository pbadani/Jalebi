package com.jalebi.api

case class Jalebi(name: String, triplets: Set[Triplets]) {

  private val nodes = new NodeMap()

  triplets.flatMap(_.values).foreach(triplet => {
    val edge = triplet.edge
    require(edge.source.id == triplet.source.id)
    require(edge.target.id == triplet.target.id)
    val sourceRef = nodes.add(triplet.source.id, triplet.source)
    val targetRef = nodes.add(triplet.target.id, triplet.target)
    sourceRef.addRelation(edge)
    targetRef.addRelation(edge)
    edge.setSourceRef(sourceRef)
    edge.setTargetRef(targetRef)
  })

  def searchNode(nodeId: Long): Option[Node] = nodes.get(nodeId)

  override def toString: String = {
    triplets.flatMap(_.values).map(triplet =>
      s"${Set(triplet.source.id, "--", triplet.target.id).mkString("\t\t")}")
      .mkString(sys.props("line.separator"))
  }
}