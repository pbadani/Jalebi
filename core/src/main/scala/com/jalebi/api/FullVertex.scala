package com.jalebi.api

case class FullVertex[T](override val id: VertexID, override protected val data: T, override protected val relations: Seq[Edge[T]]) extends Vertex[T]

object FullVertex {
  def apply[T](id: VertexID, data: T, relations: Seq[Edge[T]]): Vertex[T] = new FullVertex[T](id, data, relations)
}