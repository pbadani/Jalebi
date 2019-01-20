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
  private val scheduler = if (jContext.onLocalMaster) {
    context.actorOf(LocalScheduler.props(jContext, applicationId), LocalScheduler.name())
  } else {
    context.actorOf(Props(ApplicationMaster(jContext, null, applicationId)), "AppMaster")
  }
  private val conf = MasterSettings(context.system)
  private var stateMonitorRef: Option[ActorRef] = None

  startWith(UnInitialized, EmptyExecutorStateManager)

  when(UnInitialized) {
    case Event(InitializeExecutors, EmptyExecutorStateManager) =>
      val numOfExecutors = jContext.conf.getNumberOfExecutors().toInt
      val executorStateManage = ExecutorStateManage(jContext)
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
      sender() ! result.value.
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
      val executorIds = executorStateManage.listExecutorIds()
      stateMonitorRef = Some(context.actorOf(StateMonitor.props(executorStateManage, jContext), StateMonitor.name()))
      scheduler ! StartExecutors(executorIds, conf.hostPort)
      executorStateManage.waitForAllToRegister(10 seconds)
    case Initialized -> DatasetLoaded =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      executorStateManage.waitForAllToLoad(10 seconds)
    case _ -> Killed =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      scheduler ! StopExecutors
      executorStateManage.waitForAllToUnregister(10 seconds)
  }

  initialize()
}

object JobManager {
  def props(jContext: JalebiContext) = Props(new JobManager(jContext))

  def name = "JobManager"
}
