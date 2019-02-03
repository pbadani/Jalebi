package com.jalebi.driver

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import com.jalebi.context.{JalebiConfig, JalebiContext}
import com.jalebi.message.InitializeExecutors
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class JobManagerTest extends TestKit(ActorSystem("JobManager")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  private val conf = JalebiConfig
    .withAppName("TestApp")
    .withMaster("local")
    .withHDFSFileSystem("file", "localhost")
    .fry()

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "JobManager" must {
    "start with Uninitialized state and an empty ExecutorStateManager" in {
      val jalebiContext = JalebiContext(conf, system)
      val jobManager = system.actorOf(JobManager.props(jalebiContext), JobManager.name)
      jobManager ! InitializeExecutors
    }
  }
}
