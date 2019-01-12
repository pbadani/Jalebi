package com

import com.jalebi.api.{Edge, Node}
import com.jalebi.context.{JalebiConfig, JalebiContext, JalebiWriter}

object CreateDataset {
  def main(args: Array[String]): Unit = {
    val conf = JalebiConfig
      .withAppName("TestApp")
      .withMaster("local")
      .withHDFSFileSystem("hdfs", "localhost", 9820)
      .fry()

    val v = (0 to 100000).map(i => (i.longValue(), Node(i, Map("Key" -> s"Value$i")))).toMap
    val e = for (i <- 1 to 50000) yield {
      Edge(
        source = v((Math.random() * 10000).toLong),
        target = v((Math.random() * 10000).toLong),
        data = Map("Key" -> s"Value$i"),
        isDirected = false
      )
    }

    val context = JalebiContext(conf)
    context.createDataset(new JalebiWriter {
      override def vertices[V]: Seq[Node] = v.values.toSeq

      override def edges[E]: Seq[Edge] = e

      override def datasetName: String = "test1"
    })
  }
}
