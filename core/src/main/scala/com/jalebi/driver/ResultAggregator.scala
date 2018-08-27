package com.jalebi.driver

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.jalebi.proto.jobmanagement.TaskResponse

import scala.collection.mutable

class ResultAggregator {

  private val jobIdToResult = mutable.HashMap[String, mutable.Queue[TaskResponse]]()

  def waitForJobToBeCompleted(jobId: String, executors: Set[String]): Unit = {
    val oneWay = new CountDownLatch(executors.size)
    oneWay.await(1000, TimeUnit.SECONDS)


  }

  def saveTaskResult(taskResponse: TaskResponse): Unit = {
    jobIdToResult.getOrElseUpdate(taskResponse.jobId, mutable.Queue[TaskResponse]()).enqueue(taskResponse)
  }

  def getResultForJobId[T](jobId: String, f: TaskResponse => Seq[T]): mutable.Queue[T] = {
    jobIdToResult.getOrElseUpdate(jobId, mutable.Queue[TaskResponse]())
      .flatMap((a: TaskResponse) => f(a))
  }
}
