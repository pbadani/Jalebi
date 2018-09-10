package com.jalebi.driver

import com.jalebi.context.JalebiConfig
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class ExecutorStateManagerTest extends FlatSpec with Matchers {
  private val conf = JalebiConfig
    .withAppName("TestApp")
    .withMaster("local")
    .withHDFSFileSystem("file", "localhost", 0)
    .fry()

  "ExecutorStateManager" should "be able to list all the executors added." in {
    val executorStateManager = ExecutorStateManager(conf)

    val executorIds = Set("TestExecutor1", "TestExecutor2", "TestExecutor3")

    executorStateManager.isInitialized shouldBe false

    executorIds.foreach(executorStateManager.addExecutor)

    executorStateManager.listExecutorIds() shouldBe Set("TestExecutor1", "TestExecutor2", "TestExecutor3")
  }

  it should "be able to assign parts to executors" in {
    val executorStateManager = ExecutorStateManager(conf)

    val executorIds = Set("TestExecutor1", "TestExecutor2", "TestExecutor3")

    executorStateManager.isInitialized shouldBe false

    executorIds.foreach(executorStateManager.addExecutor)

    executorIds.foreach(executorId => {
      executorStateManager.listPartsForExecutorId(executorId) shouldBe Set.empty
    })

    val parts = (0 to 2).map(i => s"part--$i").toSet

    executorStateManager.loadPartsToExecutors("Test", parts, "Test")

    val actualParts = mutable.HashSet[String]()
    executorIds.foreach(executorId => {
      val partsForExecutor = executorStateManager.listPartsForExecutorId(executorId)
      // should not already be assigned.
      partsForExecutor.foreach(p => {
        actualParts.contains(p) shouldBe false
        actualParts.add(p)
      })
    })
    parts shouldBe actualParts
  }

  it should "uniformly assign parts to executors." in {
    val executorStateManager = ExecutorStateManager(conf)

    val executorIds = Set("TestExecutor1", "TestExecutor2", "TestExecutor3")

    executorStateManager.isInitialized shouldBe false

    executorIds.foreach(executorStateManager.addExecutor)

    executorIds.foreach(executorId => {
      executorStateManager.listPartsForExecutorId(executorId) shouldBe Set.empty
    })

    val parts = (0 to 7).map(i => s"part--$i").toSet

    executorStateManager.loadPartsToExecutors("Test", parts, "Test")

    val actualParts = mutable.HashSet[String]()
    executorIds.foreach(executorId => {
      val partsForExecutor = executorStateManager.listPartsForExecutorId(executorId)

      //8 parts split into 3 executors should be uniformly distributed as any order of (3, 3, 2)
      partsForExecutor.nonEmpty && partsForExecutor.size <= 3 shouldBe true
      // should not already be assigned.
      partsForExecutor.foreach(p => {
        actualParts.contains(p) shouldBe false
        actualParts.add(p)
      })
    })
    parts shouldBe actualParts
  }

}
