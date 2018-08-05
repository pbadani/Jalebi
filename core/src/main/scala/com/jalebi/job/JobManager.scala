package com.jalebi.job

import com.jalebi.context.JalebiContext
import com.jalebi.exception.DatasetNotLoadedException
import com.jalebi.hdfs.HDFSClient

case class JobManager(context: JalebiContext) {

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Boolean = {
    hdfsClient.getFileBlockLocations()
    false
  }

  def submitJob(job: Job): Unit = {

  }

  def newJobId(): String = s"${context.applicationID}.${context.generateNewJobID()}"
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
