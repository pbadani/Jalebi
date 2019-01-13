package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import org.scalatest.{FlatSpec, Matchers}

class ExecutorStateManagerTest extends FlatSpec with Matchers {
  private val conf = JalebiConfig
    .withAppName("TestApp")
    .withMaster("local")
    .withHDFSFileSystem("file", "localhost")
    .fry()

}
