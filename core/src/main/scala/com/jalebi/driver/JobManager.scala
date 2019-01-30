package com.jalebi.driver

import akka.actor._
import com.jalebi.common.Logging
import com.jalebi.context.JalebiContext
import com.jalebi.exception.DatasetNotFoundException
import com.jalebi.executor.LocalScheduler
import com.jalebi.extensions.MasterSettings
import com.jalebi.hdfs.HDFSClient
import com.jalebi.message._
import com.jalebi.yarn.ApplicationMaster
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.concurrent.duration._

case class JobManager(jContext: JalebiContext) extends FSM[JobManagerState, JobManagerData] with Logging {

  private val applicationId = s"Jalebi-${System.currentTimeMillis()}"
  private val conf = MasterSettings(context.system)
  private val executorStateManage = ExecutorStateManage(jContext)
  private val stateMonitorRef = context.actorOf(StateMonitor.props(executorStateManage, jContext), StateMonitor.name())
  private val scheduler = if (jContext.onLocalMaster) {
    context.actorOf(LocalScheduler.props(applicationId, stateMonitorRef, conf.hostPort), LocalScheduler.name())
  } else {
    context.actorOf(ApplicationMaster.props(applicationId, stateMonitorRef, conf.hostPort), ApplicationMaster.name())
  }

  startWith(UnInitialized, EmptyExecutorStateManager)

  when(UnInitialized) {
    case Event(InitializeExecutors, EmptyExecutorStateManager) =>
      val numOfExecutors = jContext.conf.getNumberOfExecutors().toInt
      (0 until numOfExecutors).foreach(_ => {
        val executorId = jContext.newExecutorId(applicationId)
        executorStateManage.addExecutor(executorId, ExecutorStateManage.default)
      })
      goto(Initialized) using executorStateManage
  }

  when(Initialized) {
    case Event(l: LoadDataset, e) =>
      val name = l.name
      val hdfsClient = HDFSClient.withDistributedFileSystem(jContext.conf.hdfsHostPort, new YarnConfiguration())
      if (!hdfsClient.datasetExists(name)) {
        throw new DatasetNotFoundException(s"Dataset '$name' not found.")
      }
      val parts = hdfsClient.listDatasetParts(name)
      val executorStateManage = e.asInstanceOf[ExecutorStateManage]
      executorStateManage.loadPartsToExecutors(jContext.newJobId(applicationId), parts, name)
      goto(DatasetLoaded) using executorStateManage
  }

  when(DatasetLoaded) {
    case Event(f: FindNode, e) =>
      val executorStateManage = e.asInstanceOf[ExecutorStateManage]
      val jobId = jContext.newJobId(applicationId)
      val result = executorStateManage.produceNewBlockingJob(f.copy(jobId = jobId))
      LOGGER.info(s"Returning result for $jobId.")
      //      implicit val ec = context.dispatcher
      //      result onComplete {
      //        case scala.util.Success(value) => sender() ! value
      //        case scala.util.Failure(exception) =>
      //          LOGGER.error(s"Exception occurred in job $jobId. message: ${exception.getMessage}", exception)
      //          sender() ! Seq.empty
      //      }
      sender() ! result
      stay
  }

  when(Killed) {
    case Event("", s) =>
      val a = ""
      stay
  }

  whenUnhandled {
    case Event(Shutdown, e) =>
      val executorStateManage = e.asInstanceOf[ExecutorStateManage]
      goto(Killed) using executorStateManage
  }

  onTransition {
    case UnInitialized -> Initialized =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      LOGGER.info(s"Starting executors ${executorStateManage.listExecutorIds().mkString(", ")}")
      scheduler ! StartExecutors(executorStateManage.listExecutorIds())
      executorStateManage.waitForAllToRegister(30 seconds)
    case Initialized -> DatasetLoaded =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      executorStateManage.waitForAllToLoad(30 seconds)
    case _ -> Killed =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      scheduler ! StopExecutors
      executorStateManage.waitForAllToUnregister(30 seconds)
  }

  initialize()
}

object JobManager {
  def props(jContext: JalebiContext) = Props(new JobManager(jContext))

  def name = "JobManager"
}
