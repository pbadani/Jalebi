package com.jalebi.api

case class FullVertex[T](override protected val id: VertexID, override protected val data: T, override protected val relations: Seq[Edge[T]]) extends Vertex[T]

object FullVertex {
  def apply[T](id: VertexID, data: T, relations: Seq[Edge[T]]): Vertex[T] = new PartialVertex[T](id, data, relations)
}
