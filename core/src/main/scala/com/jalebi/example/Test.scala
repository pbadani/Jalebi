package com.jalebi.example

import com.jalebi.api._
import com.jalebi.context.{JalebiConfig, JalebiContext}

class Test {

  def main(args: Array[String]): Unit = {

    val jalebiConfig = JalebiConfig
      .withAppName("Jalebi")
      .withMaster("jalebi://localhost:8080")
      .fry()

    val jalebiContext = JalebiContext(jalebiConfig)

    val (vertices, edges) = getTestData

    jalebiContext.load(vertices, edges)

  }

  def getTestData: (Vertices[Long], Edges[Long]) = {
    val vertices = Vertices(
      Set[Vertex[Long]](
        FullVertex[Long](VertexID(1), 1, null),
        FullVertex[Long](VertexID(2), 2, null),
        FullVertex[Long](VertexID(3), 3, null),
        FullVertex[Long](VertexID(4), 4, null),
        FullVertex[Long](VertexID(5), 5, null),
        FullVertex[Long](VertexID(6), 6, null),
        FullVertex[Long](VertexID(7), 7, null)
      )
    )
    val edges = Edges(
      Set.empty[Edge[Long]]
    )
    (vertices, edges)
  }
}
