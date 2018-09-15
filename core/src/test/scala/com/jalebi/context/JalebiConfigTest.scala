package com.jalebi.context

import org.scalatest.{FlatSpec, Matchers}

class JalebiConfigTest extends FlatSpec with Matchers {

  "Config" should "be built with correct attributes passed in the builder" in {
    val conf = JalebiConfig
      .withAppName("TestApp")
      .withMaster("local")
      .withHDFSFileSystem("file", "localhost")
      .fry()
    conf.appName shouldBe "TestApp"
    conf.master shouldBe "local"
    conf.hdfsHostPort.isDefined shouldBe true
    val hostport = conf.hdfsHostPort.get
    hostport.scheme shouldBe "file"
    hostport.host shouldBe "localhost"
    hostport.port shouldBe 0
  }

  it should "be built with master as mandatory field." in {
    a[IllegalArgumentException] should be thrownBy {
      JalebiConfig
        .withAppName("TestApp")
        //.withMaster("local")
        .withHDFSFileSystem("file", "localhost")
        .fry()
    }
  }

  it should "be built with master as mandatory field for non local scheme" in {
    val conf = JalebiConfig
      .withAppName("TestApp")
      .withMaster("jalebi://localhost:8080")
      .withHDFSFileSystem("file", "localhost")
      .fry()

    conf.appName shouldBe "TestApp"
    conf.master shouldBe "jalebi://localhost:8080"
    conf.hdfsHostPort.isDefined shouldBe true
    val hostport = conf.hdfsHostPort.get
    hostport.scheme shouldBe "file"
    hostport.host shouldBe "localhost"
    hostport.port shouldBe 0
  }

  it should "be built with scheme jalebi if it is not local." in {
    a[IllegalArgumentException] should be thrownBy {
      JalebiConfig
        .withAppName("")
        .withMaster("jalebi1://localhost:8080")
        .withHDFSFileSystem("file", "localhost")
        .fry()
    }
  }

  it should "be built with mandatory app name." in {
    a[IllegalArgumentException] should be thrownBy {
      JalebiConfig
        .withAppName("")
        .withMaster("local")
        .withHDFSFileSystem("file", "localhost")
        .fry()
    }
  }

  it should "be built with mandatory scheme and host-port." in {
    a[IllegalArgumentException] should be thrownBy {
      JalebiConfig
        .withAppName("")
        .withMaster("local")
        .fry()
    }
  }
}
