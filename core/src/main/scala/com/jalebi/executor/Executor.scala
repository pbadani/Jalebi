package com.jalebi.executor

import akka.actor.{ActorSelection, ActorSystem, FSM, Props, Timers}
import com.jalebi.common.Logging
import com.jalebi.extensions.ExecutorSettings
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.message._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

case class Executor(taskManager: TaskManager, driverHostPort: RichHostPort) extends FSM[ExecutorState, ExecutorData] with Timers with Logging {

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
    monitorRef.get ! RegisterExecutor(taskManager.executorId)
  }

  override def postStop(): Unit = {
    super.postStop()
    LOGGER.info(s"Executor ${taskManager.executorId} unregistering.")
    monitorRef.get ! UnregisterExecutor(taskManager.executorId)
  }

  startWith(New, Empty)

  when(New) {
    case Event(RegistrationAcknowledged(hdfs), _) =>
      LOGGER.info(s"Registered ${taskManager.executorId}")
      require(monitorRef.isDefined, "MonitorRef not set.")
      goto(Registered) using ExecutorConfig(monitorRef.get, hdfs)
  }

  when(Registered) {
    case Event(LoadDataset(name), s) =>
      LOGGER.info(s"Loading dataset $name.")
      goto(Loaded) using stateData
  }

  onTransition {
    case New -> Registered =>
      timers.startPeriodicTimer(HeartbeatKey, Heartbeat(taskManager.executorId), 5 seconds)
    case Registered -> Loaded =>
      sender() ! LoadedDataset(taskManager.executorId)
  }

  whenUnhandled {
    case Event(h@Heartbeat(name), _) =>
      monitorRef.get ! h
      stay
  }

  initialize()

  //  override def run(): Unit = {
  //    val channel = ManagedChannelBuilder
  //      .forAddress(driverHostPort.host, driverHostPort.port.toInt)
  //      .usePlaintext().build()
  //
  //    LOGGER.info(s"Registering executor ${taskManager.executorId}.")
  //    val stub = JobManagementProtocolGrpc.stub(channel)
  //    stub.registerExecutor(ExecutorRequest(taskManager.executorId)).onComplete(r => {
  //      taskManager.markRegistered(TaskConfig(r.get))
  //    })
  //    LOGGER.info(s"Registered executor ${taskManager.executorId}.")
  //
  //    val resp: StreamObserver[TaskResponse] = stub.startTalk(
  //      new StreamObserver[TaskRequest] {
  //        override def onError(t: Throwable): Unit = {
  //          LOGGER.error(s"on error - Executor ${taskManager.executorId} ${t.getMessage} ${t.getCause} ${t.getStackTrace}")
  //        }
  //
  //        override def onCompleted(): Unit = {
  //          LOGGER.info(s"on Complete - Executor ${taskManager.executorId}.")
  //        }
  //
  //        override def onNext(taskRequest: TaskRequest): Unit = {
  //          LOGGER.info(s"on next - Executor ${taskManager.executorId} $taskRequest.")
  //          taskManager.execute(taskRequest)
  //        }
  //      })
  //    try {
  //      while (taskManager.keepRunning) {
  //        resp.onNext(taskManager.propagateInHeartbeat.get)
  //        Thread.sleep(taskManager.heartbeatInterval * 1000)
  //      }
  //    } catch {
  //      case _: InterruptedException =>
  //        LOGGER.info(s"Request to shutdown executor ${taskManager.executorId}")
  //        stub.unregisterExecutor(ExecutorRequest(taskManager.executorId)).onComplete(r => {
  //          taskManager.markUnregistered(TaskConfig(r.get))
  //        })
  //    }
  //  }

  def terminate(): Unit = {
    taskManager.keepRunning
  }
}

object Executor extends Logging {

  val master = ActorSystem("Executors", ConfigFactory.load("executor"))

  def props(executorId: String, driverHostPort: RichHostPort) = Props(Executor(TaskManager(executorId), driverHostPort))

  def name(executorId: String): String = executorId

  def main(args: Array[String]): Unit = {
    val executorArgs = ExecutorArgs(args)
    LOGGER.info(s"Starting Executor with Args $executorArgs")
    val executorRef = Executor.master.actorOf(Executor.props(executorArgs.getExecutorId, executorArgs.getDriverHostPort), Executor.name(executorArgs.getExecutorId))
  }
}
