package com.jalebi.driver

import com.jalebi.proto.jobmanagement.TaskResponse
import com.jalebi.proto.jobmanagement.TaskState.COMPLETED

import scala.collection.immutable.Queue
import scala.collection.mutable

class ResultAggregator {

  private val jobIdToResult = mutable.HashMap[String, Queue[TaskResponse]]()
    .withDefaultValue(Queue[TaskResponse]())

  def waitForJobToBeCompleted(jobId: String, executors: Set[String]): Unit = {
    val responses = jobIdToResult(jobId)
    while (
      !executors.forall(thisExecutorId => responses.exists(response => {
        response.executorId == thisExecutorId && response.taskState == COMPLETED
      }))
    ) {
      Thread.sleep(1000)
    }
  }

  def saveTaskResult(taskResponse: TaskResponse): Unit = {
    jobIdToResult(taskResponse.jobId).enqueue(taskResponse)
  }

  def getResultForJobId[T](jobId: String, f: TaskResponse => Seq[T]): Queue[T] = {
    jobIdToResult(jobId).flatMap((a: TaskResponse) => f(a))
  }
}
