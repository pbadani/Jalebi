package com.jalebi.context

import com.jalebi.api.{Edge, Vertex}

trait JalebiWriter {

  def vertices[V]: Seq[Vertex]

  def edges[E]: Seq[Edge]

  def datasetName: String
}
