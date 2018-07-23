package com.hdfs

import java.io.InputStream
import java.net.URL

import org.apache.hadoop.fs.FsUrlStreamHandlerFactory
import org.apache.hadoop.io.IOUtils


object URLCat {

  def main(args: Array[String]): Unit = {
    URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory())

    var stream: Option[InputStream] = None
    try {
      stream = Some(new URL("hdfs://localhost/user/paragb/something.txt").openStream())
      IOUtils.copyBytes(stream.get, System.out, 4096, false)
    } finally {
      if (stream.isDefined) {
        IOUtils.closeStream(stream.get)
      }
    }
  }
}
