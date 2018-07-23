package com.hdfs

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}

object ListStatus {
  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    val fs = FileSystem.get(URI.create("hdfs://localhost"), conf)
    val fStatus = fs.listStatus(new Path("/"))
    FileUtil.stat2Paths(fStatus).foreach(println)
  }
}
