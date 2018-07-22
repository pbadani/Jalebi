package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api._
import com.jalebi.api.exception.DatasetNotFoundException
import com.jalebi.job.JobManager

case class JalebiContext private(conf: JalebiConfig) {

  val applicationID = s"Jalebi_App_${System.currentTimeMillis()}"
  val jobIDCounter = new AtomicLong(0)
  private val jobManager = JobManager.createNew(this)

  @throws[IllegalArgumentException]
  private def validate[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = {
    //    val vertexID
    null
  }

  @throws[IllegalArgumentException]
  def load[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = {
    validate(vertices, edges)
    Jalebi.createLocal(this, vertices, edges)
  }

  @throws[DatasetNotFoundException]
  def load[V, E](datasetName: String): Jalebi[V, E] = {
    //    validate(vertices, edges)
    //    Jalebi.createLocal(vertices, edges)
    null
  }

  def generateNewJobID(): Long = jobIDCounter.getAndIncrement()
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
