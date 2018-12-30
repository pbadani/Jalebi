package com.jalebi.executor

import akka.actor.{ActorSelection, ActorSystem, FSM, PoisonPill, Props, Timers}
import com.jalebi.common.Logging
import com.jalebi.extensions.ExecutorSettings
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.message._
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.concurrent.duration._

case class Executor(executorId: String, driverHostPort: RichHostPort) extends FSM[ExecutorState, ExecutorData] with Timers with Logging {

  private val conf = ExecutorSettings(context.system)
  //  private var masterRef: Option[ActorSelection] = None
  private var monitorRef: Option[ActorSelection] = None

  override def preStart(): Unit = {
    super.preStart()

    def pathToRemote(actor: String): String = {
      val host = driverHostPort.host
      val port = driverHostPort.port
      val protocol = conf.protocol
      val systemName = conf.masterSystem
      s"$protocol://$systemName@$host:$port/$actor"
    }

    //    masterRef = Some(Executor.master.actorSelection(pathToRemote(conf.masterActor)))
    monitorRef = Some(Executor.master.actorSelection(pathToRemote(conf.monitorActor)))
    monitorRef.get ! RegisterExecutor(executorId)
  }

  override def postStop(): Unit = {
    super.postStop()
    LOGGER.info(s"Executor $executorId unregistering.")
    monitorRef.get ! UnregisterExecutor(executorId)
  }

  startWith(New, Empty)

  when(New) {
    case Event(RegistrationAcknowledged(hdfs), _) =>
      LOGGER.info(s"Registered $executorId")
      require(monitorRef.isDefined, "MonitorRef not set.")
      goto(Registered) using RegisteredExecutorState(monitorRef.get, hdfs)
  }

  when(Registered) {
    case Event(LoadDatasetTask(jobId, name, parts), s) =>
      val state = s.asInstanceOf[RegisteredExecutorState]
      LOGGER.info(s"Loading dataset $name.")
      val hdfsClient = HDFSClient.withDistributedFileSystem(Some(state.hdfs), new YarnConfiguration())
      val jalebi = hdfsClient.loadDataset(name, parts)
      goto(Loaded) using LoadedExecutorState(state.monitorRef, state.hdfs, jalebi)
  }

  when(Loaded) {
    case Event(FindNodeTask(jobId, nodeId), s) =>
      val executorState = s.asInstanceOf[LoadedExecutorState]
      LOGGER.info(s"Finding node $nodeId in $executorId.")
      val result = executorState.jalebi.searchNode(nodeId)
      monitorRef.get ! TaskResult(jobId, result.map(Set(_)).getOrElse(Set.empty))
      stay using executorState
  }

  onTransition {
    case New -> Registered =>
      timers.startPeriodicTimer(HeartbeatKey, Heartbeat(executorId), 3 seconds)
    case Registered -> Loaded =>
      monitorRef.get ! LoadedDataset(executorId)
  }

  whenUnhandled {
    case Event(h@Heartbeat(name), _) =>
      monitorRef.get ! h
      stay
    case Event(ShutExecutors, _) =>
      //perform cleanup, save state, flush results.
      self ! PoisonPill
      stay
  }

  initialize()

}

object Executor extends Logging {

  val master = ActorSystem("Executors", ConfigFactory.load("executor"))

  def props(executorId: String, driverHostPort: RichHostPort) = Props(Executor(executorId, driverHostPort))

  def name(executorId: String): String = executorId

  def main(args: Array[String]): Unit = {
    val executorArgs = ExecutorArgs(args)
    LOGGER.info(s"Starting Executor with Args $executorArgs")
    Executor.master.actorOf(Executor.props(executorArgs.getExecutorId, executorArgs.getDriverHostPort), Executor.name(executorArgs.getExecutorId))
  }
}
