package com

import com.jalebi.api.{Edge, Node, Node}
import com.jalebi.context.{JalebiConfig, JalebiContext, JalebiWriter}

object CreateDataset {
  def main(args: Array[String]): Unit = {
    val conf = JalebiConfig
      .withAppName("TestApp")
      .withMaster("local")
//      .withHDFSFileSystem("file", "localhost", 9820)
      .withHDFSFileSystem("hdfs", "localhost", 9820)
      .fry()

    val v = (0 to 10000).map(i => Node(Node(i), Map("Key" -> s"Value$i")))
    val e = for (i <- 1 to 5000) yield {
      Edge(Node((Math.random() * 1000).toLong), Node((Math.random() * 1000).toLong), Map("Key" -> s"Value$i"), isDirected = false)
    }

    val context = JalebiContext(conf)
    context.createDataset(new JalebiWriter {
      override def vertices[V]: Seq[Node] = v

      override def edges[E]: Seq[Edge] = e

      override def datasetName: String = "test"
    })
  }
}
