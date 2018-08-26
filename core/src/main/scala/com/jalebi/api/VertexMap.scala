package com.jalebi.api

import scala.collection.mutable

class VertexMap {
  private val vertices = mutable.HashMap[VertexID, Vertex]()

  def add(id: VertexID, element: Vertex): Vertex = {
    vertices.getOrElseUpdate(id, element)
  }

  def get(id: VertexID): Option[Vertex] = {
    vertices.get(id)
  }
}
