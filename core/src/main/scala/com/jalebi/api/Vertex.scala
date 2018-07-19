package com.jalebi.api

trait Vertex[T] extends Serializable {

  protected val id: VertexID

  protected val data: T

  protected val relations: Seq[Edge[T] with EdgeDirection with EdgeWeight]

  def getOutgoing: Seq[Edge[T] with EdgeDirection with EdgeWeight] = {
    relations.filter(e => (e.isDirected && e.isSource(this.id))
      || (!e.isDirected))
  }

  def getIncoming: Seq[Edge[T] with EdgeDirection with EdgeWeight] = {
    relations.filter(e => (e.isDirected && e.isTarget(this.id))
      || (!e.isDirected))
  }

  override def toString: String = {
    s"VertexID: ${id}"
  }
}

