package com.jalebi.api

case class VertexID(id: Long)

object VertexID {
  def apply(id: Long) = new VertexID(id)
}
