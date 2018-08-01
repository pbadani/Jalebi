package com.jalebi.input

import com.jalebi.api.{Edge, Triplet, Vertex}

abstract class JalebiLoader[V, E](config: JalebiLoaderConfig) {

  def getVertices: Set[Vertex[V]]

  def getEdges: Set[Edge[E]]

  def load(name: String): Unit = {
    val vertices = getVertices.map(vertex => (vertex.id, vertex)).toMap
    val triplets = getEdges.map(edge => {
      Triplet(vertices.getOrElse(edge.source, null), edge, vertices.getOrElse(edge.target, null))
    })
  }

}
