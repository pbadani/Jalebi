package com.jalebi.context

import com.jalebi.api.{Edge, Vertex}

trait Inputter {

  def listVertices[V]: Seq[Vertex]

  def listEdges[E]: Seq[Edge]

  def datasetName: String
}
