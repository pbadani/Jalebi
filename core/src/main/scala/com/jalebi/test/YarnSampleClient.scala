package com.jalebi.test

import com.jalebi.context.{JalebiConfig, JalebiContext}

object YarnSampleClient {
  def main(args: Array[String]): Unit = {
    val jconf = JalebiConfig.withAppName("FirstApp").withMaster("local").withHDFSFileSystem("hdfs", "localhost", 9820).fry()
    val jcontext = JalebiContext(jconf)
    val dataset = jcontext.loadDataset("test")
    val r = dataset.findNode(10)
    //    jcontext.close()
    //    println(r)
  }
}
