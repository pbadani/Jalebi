package com.jalebi.driver

import com.jalebi.common.Logging
import com.jalebi.proto.jobmanagement.TaskResponse
import com.jalebi.proto.jobmanagement.TaskState.COMPLETED

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ResultAggregator(executorStateManager: ExecutorStateManager) extends Logging {

  private val jobIdToResult = mutable.HashMap[String, mutable.Queue[TaskResponse]]()
  //    .withDefaultValue(mutable.Queue[TaskResponse]())

  def waitForJobToBeCompleted(jobId: String): Unit = {
    val executors = executorStateManager.listExecutorIds()
    val futures = executors.map(executorId => Future {
      var isTaskCompleted = false
      while ( {
        Thread.sleep(1000)
        !isTaskCompleted
      }) {
        if (jobIdToResult.contains(jobId)) {
          val resultQueue = jobIdToResult(jobId)
          isTaskCompleted = resultQueue.exists(response => {
            response.executorId == executorId && response.taskState == COMPLETED
          })
        }
      }
    })
    Await.result(Future.sequence(futures), 100 second)
    LOGGER.info(s"JobId $jobId completed for all executors ${executors.mkString(", ")}")
  }

  def saveTaskResult(taskResponse: TaskResponse): Unit = synchronized {
    if (taskResponse.jobId.isEmpty) {
      //no task result to save.
      return
    }
    if (!jobIdToResult.contains(taskResponse.jobId)) {
      jobIdToResult += (taskResponse.jobId -> mutable.Queue[TaskResponse]())
    }
    jobIdToResult(taskResponse.jobId).enqueue(taskResponse)
  }

  def getResultForJobId[T](jobId: String, f: TaskResponse => Seq[T]): mutable.Queue[T] = {
    waitForJobToBeCompleted(jobId)
    jobIdToResult(jobId).flatMap((a: TaskResponse) => {
      LOGGER.info(s"Result $a")
      f(a)
    })
  }
}
