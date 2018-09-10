package com.jalebi.common

import org.apache.hadoop.yarn.api.ApplicationConstants.Environment
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.scalatest.{FlatSpec, Matchers}

class YarnUtilsTest extends FlatSpec with Matchers {

  "YarnUtils" should "add resources to classpath of the environment variables map." in {
    val resources = Set("TestResourceName1", "TestResourceName2", "TestResourceName3")
    val additionalEntries = Map("key1" -> "value1", "key2" -> "value2")
    val classpathKey = Environment.CLASSPATH.name
    val actual = YarnUtils.createEnvironmentVariables(new YarnConfiguration(), resources, additionalEntries)
    val actualClasspath = actual(classpathKey)
    actualClasspath.contains("TestResourceName1<CPS>TestResourceName2<CPS>TestResourceName3") shouldBe true
    actual("key1") shouldBe "value1"
    actual("key2") shouldBe "value2"
  }
}
