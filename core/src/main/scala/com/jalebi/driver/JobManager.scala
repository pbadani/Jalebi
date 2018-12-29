package com.jalebi.driver

import akka.actor._
import com.jalebi.api.{Vertex, VertexID}
import com.jalebi.common.Logging
import com.jalebi.context.{Dataset, JalebiContext}
import com.jalebi.exception.{DatasetNotFoundException, DatasetNotLoadedException}
import com.jalebi.executor.LocalScheduler
import com.jalebi.extensions.MasterSettings
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.message._
import com.jalebi.proto.jobmanagement.HostPort
import com.jalebi.yarn.ApplicationMaster
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.collection.mutable
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
    case Event(LoadDataset(name), e) =>
      val hdfsClient = HDFSClient.withDistributedFileSystem(jContext.conf.hdfsHostPort, new YarnConfiguration())
      if (!hdfsClient.datasetExists(name)) {
        throw new DatasetNotFoundException(s"Dataset '$name' not found.")
      }
      val executorStateManage = e.asInstanceOf[ExecutorStateManage]
      val parts = hdfsClient.listDatasetParts(name)
      val jobId = jContext.newJobId(applicationId)
      executorStateManage.loadPartsToExecutors(jobId, parts, name)
      goto(DatasetLoaded) using executorStateManage
  }

  when(DatasetLoaded) {
    case Event("", s) =>
      val a = ""
      stay
  }

  onTransition {
    case UnInitialized -> Initialized =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      val hostPort = RichHostPort(HostPort("", conf.host, conf.port))
      val executorIds = executorStateManage.listExecutorIds()
      stateMonitorRef = Some(context.actorOf(StateMonitor.props(executorStateManage, jContext), StateMonitor.name()))
      scheduler ! StartExecutors(executorIds, hostPort)
      executorStateManage.waitForAllToRegister(10 seconds)
    case Initialized -> DatasetLoaded =>
      val executorStateManage = nextStateData.asInstanceOf[ExecutorStateManage]
      executorStateManage.waitForAllToLoad(10 seconds)
  }

  initialize()

  //
  //  def ensureInitialized(): Unit = synchronized {
  //    if (!executorState.isInitialized) {
  //      driver.start()
  //      val executorIds = executorState.listExecutorIds()
  //      if (executorIds.isEmpty) {
  //        throw new IllegalStateException("No executors to run.")
  //      }
  //      LOGGER.info(s"Starting executors: [${executorIds.mkString(", ")}]")
  //      scheduler.startExecutors(executorIds)
  //      executorState.initialize1()
  //    }
  //  }


  @throws[DatasetNotLoadedException]
  def ensureDatasetLoaded(name: String): Unit = {
    if (!jContext.isLoaded) {
      throw new DatasetNotLoadedException("No dataset is loaded currently.")
    }
    if (jContext.getCurrentDatasetName != name) {
      throw new DatasetNotLoadedException(s"Currently loaded dataset ${jContext.getCurrentDatasetName} is not same as $name")
    }
  }

  @throws[DatasetNotLoadedException]
  def load(hdfsClient: HDFSClient, name: String): Dataset = {
    //    ensureInitialized()
    //    val jobId = jContext.newJobId(applicationId)
    //    jobActors.put(jobId, context.actorOf(Job.props(jobId), Job.name(jobId)))
    //    val parts = hdfsClient.listDatasetParts(name)
    //    executorState.loadPartsToExecutors(jobId, parts, name)
    //    resultAggregator.waitForJobToBeCompleted(jobId)
    Dataset(name, this)
  }

  def find(vertexId: VertexID, name: String): mutable.Queue[Vertex] = {
    ensureDatasetLoaded(name)
    null
    //    val jobId = jContext.newJobId(applicationId)
    //    executorState.assignNewTask(TaskRequestBuilder.searchRequest(jobId, vertexId, name))
    //    val responseToVertexes: TaskResponse => Seq[Vertex] = response => ResultConverter.convertFromVertices(response.vertexResults)
    //    resultAggregator.getResultForJobId(jobId, responseToVertexes)
  }


  def driverHostPort: RichHostPort = jContext.driverHostPort

}

object JobManager {
  def props(jContext: JalebiContext) = Props(new JobManager(jContext))

  def name = "JobManager"
}
