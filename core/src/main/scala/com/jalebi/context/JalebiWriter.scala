package com.jalebi.context

import com.jalebi.api.{Edge, Node}

trait JalebiWriter {

  def vertices[V]: Seq[Node]

  def edges[E]: Seq[Edge]

  def datasetName: String
}
