package com.jalebi.common

import org.apache.hadoop.yarn.api.ApplicationConstants.Environment
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class JalebiUtilsTest extends FlatSpec with Matchers {

  val applicationId = "TestApplicationId"
  val resource = "TestResourceName"
  val resources = Seq("TestResourceName1", "TestResourceName2", "TestResourceName3")

  "JalebiUtils" should "return the base directory for a particular application id and a resource." in {
    val expectedPath = "jalebi/TestApplicationId/TestResourceName"
    val actualPath = JalebiUtils.getResourcePath(applicationId, resource)
    expectedPath shouldBe actualPath
  }

  it should "return the home path for an application id." in {
    val expectedPath = "jalebi/TestApplicationId/jalebihome/TestResourceName"
    val actualPath = JalebiUtils.getJalebiHomePath(applicationId, resource)
    expectedPath shouldBe actualPath
  }

  it should "build the local file system URI for the given resource." in {
    val expectedPath = s"file://$resource"
    val actualPath = JalebiUtils.URIForLocalFile(resource)
    expectedPath shouldBe actualPath
  }

  it should "add entries to classpath for the given resources." in {
    val expectedEnvironment = mutable.HashMap[String, String]((Environment.CLASSPATH.name, "TestResourceName1<CPS>TestResourceName2<CPS>TestResourceName3"))
    val actualEnvironment = mutable.HashMap[String, String]()
    resources.foreach(resource => JalebiUtils.addToClasspath(actualEnvironment, resource))
    expectedEnvironment shouldBe actualEnvironment
  }

}
