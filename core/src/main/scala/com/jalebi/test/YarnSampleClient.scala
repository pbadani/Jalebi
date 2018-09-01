package com.jalebi.test

import com.jalebi.api.VertexID
import com.jalebi.context.{JalebiConfig, JalebiContext}

object YarnSampleClient {
  def main(args: Array[String]): Unit = {
    val jconf = JalebiConfig.withAppName("FirstApp").withMaster("yarn").withHDFSFileSystem("hdfs", "localhost", 8020).fry()
    val jcontext = JalebiContext(jconf)
    val dataset = jcontext.loadDataset("Test2")
    val r = dataset.findVertex(VertexID(10))
    println(r)
  }
}