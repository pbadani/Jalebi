package com.jalebi.driver

import com.jalebi.proto.jobmanagement.TaskResponse

import scala.collection.mutable

class ResultAggregator {

  private val jobIdToResult = mutable.HashMap[String, mutable.Queue[TaskResponse]]()

  def saveTaskResult(taskResponse: TaskResponse): Unit = {
    jobIdToResult.getOrElseUpdate(taskResponse.jobId, mutable.Queue[TaskResponse]()).enqueue(taskResponse)
  }


  def getResultForJobId[T](jobId: String, f: TaskResponse => T): Seq[T] = {
    jobIdToResult
      .getOrElseUpdate(jobId, mutable.Queue[TaskResponse]())
      .map(f(_))
  }
}
