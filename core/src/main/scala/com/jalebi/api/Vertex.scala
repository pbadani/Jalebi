package com.jalebi.api

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class VertexID(id: Long)

case class Vertex(id: VertexID, data: Map[String, String]) {

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
    relations.filter(e => (e.isDirected && e.isSource(this.id))
      || (!e.isDirected))
  }

  def getIncoming: Seq[Edge] = {
    relations.filter(e => (e.isDirected && e.isTarget(this.id))
      || (!e.isDirected))
  }

  override def toString: String = s"VertexID: $id"
}

