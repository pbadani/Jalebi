package com.jalebi.api

import scala.collection.mutable

class NodeMap {
  private val vertices = mutable.HashMap[Long, Node]()

  def add(id: Long, element: Node): Node = {
    vertices.getOrElseUpdate(id, element)
  }

  def get(id: Long): Option[Node] = {
    vertices.get(id)
  }
}
