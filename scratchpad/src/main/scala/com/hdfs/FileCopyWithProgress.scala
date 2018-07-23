package com.hdfs

import java.io.BufferedInputStream
import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils

object FileCopyWithProgress {
  def main(args: Array[String]): Unit = {
    val fs = FileSystem.get(URI.create("hdfs://localhost/"), new Configuration())
    val out = fs.create(new Path("testCopied.txt"), () => System.out.print("."))
    //    val in = new BufferedInputStream(new FileInputStream("/Users/paragb/home/code/Jalebi/scratchpad/src/main/resources/hdfs/test.txt"))
    val in = new BufferedInputStream(this.getClass.getResourceAsStream("./resources/hdfs/test.txt"))
    IOUtils.copyBytes(in, out, 4096, false)
  }
}
