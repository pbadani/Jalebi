package com.jalebi.test

import com.jalebi.context.{JalebiConfig, JalebiContext}

object YarnSampleClient {
  def main(args: Array[String]): Unit = {
//    val jconf = JalebiConfig.withAppName("FirstApp").withMaster("local").withHDFSFileSystem("hdfs", "localhost", 9820).fry()
    val jconf = JalebiConfig.withAppName("FirstApp").withMaster("jalebi://localhost:8088").withHDFSFileSystem("hdfs", "localhost", 9820).fry()
    val jcontext = JalebiContext(jconf)
    val dataset = jcontext.loadDataset("test1")
    val r = dataset.findNode(10)

    r.foreach(
      println(_)
    )
    //    jcontext.close()
    //    println(r)
  }
}
