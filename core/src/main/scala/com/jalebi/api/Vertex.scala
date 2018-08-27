package com.jalebi.api

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class VertexID(id: Long)

case class Vertex(vertexId: VertexID, data: Map[String, String]) {

  private val relations = ListBuffer[Edge]()

  private val visitedByJobIDs: mutable.Set[String] = new mutable.HashSet[String]()

  def addRelation(edge: Edge): Unit = {
    relations += edge
  }

  def markVisitedByJob(jobID: String): Unit = {
    require(jobID.nonEmpty)
    visitedByJobIDs += jobID
  }

  def isVisitedByJob(jobID: String): Boolean = {
    require(jobID.nonEmpty)
    visitedByJobIDs.contains(jobID)
  }

  def getOutgoing: Seq[Edge] = {
    relations.filter(e => (e.isDirected && e.isSource(this.vertexId))
      || (!e.isDirected))
  }

  def getIncoming: Seq[Edge] = {
    relations.filter(e => (e.isDirected && e.isTarget(this.vertexId))
      || (!e.isDirected))
  }

  def getId: Long = vertexId.id

  override def toString: String = s"VertexID: $vertexId"
}

