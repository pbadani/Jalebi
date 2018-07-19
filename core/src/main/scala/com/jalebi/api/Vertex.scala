package com.jalebi.api

import scala.collection.mutable

trait Vertex[T] extends Serializable {

  protected val id: VertexID

  protected val data: T

  protected val relations: Seq[Edge[T] with EdgeDirection with EdgeWeight]

  private val visitedByJobIDs: mutable.Set[String] = new mutable.HashSet[String]()

  def markVisitedByJob(jobID: String): Unit = {
    require(jobID.nonEmpty)
    visitedByJobIDs += jobID
  }

  def isVisitedByJob(jobID: String): Boolean = {
    require(jobID.nonEmpty)
    visitedByJobIDs.contains(jobID)
  }

  def getOutgoing: Seq[Edge[T] with EdgeDirection with EdgeWeight] = {
    relations.filter(e => (e.isDirected && e.isSource(this.id))
      || (!e.isDirected))
  }

  def getIncoming: Seq[Edge[T] with EdgeDirection with EdgeWeight] = {
    relations.filter(e => (e.isDirected && e.isTarget(this.id))
      || (!e.isDirected))
  }

  override def toString: String = {
    s"VertexID: $id"
  }
}

