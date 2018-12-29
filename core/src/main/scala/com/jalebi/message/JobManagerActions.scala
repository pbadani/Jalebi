package com.jalebi.message

import com.jalebi.api.VertexID

trait Job

case class LoadDataset(name: String) extends Job

case class FindVertex(vertexId: VertexID) extends Job

object InitializeExecutors

object Shutdown
