package com.hdfs

import java.io.InputStream
import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils

object FileSystemCat {
  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    val fs = FileSystem.get(URI.create("hdfs://127.0.0.1"), conf)
    var stream: Option[InputStream] = None
    try {
      stream = Some(fs.open(new Path("something.txt"), 4096))
      IOUtils.copyBytes(stream.get, System.out, 4096, false)
    } finally {
      stream.foreach(IOUtils.closeStream(_))
    }
  }
}
