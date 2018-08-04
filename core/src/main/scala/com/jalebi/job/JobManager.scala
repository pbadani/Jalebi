package com.jalebi.job

import com.jalebi.context.JalebiContext

case class JobManager(context: JalebiContext) {
  val jobID = s"${context.applicationID}.${context.generateNewJobID()}"
}

object JobManager {
  def createNew(context: JalebiContext) = new JobManager(context)
}
