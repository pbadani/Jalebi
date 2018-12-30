package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.jalebi.api.{Triplet, Triplets}
import com.jalebi.common.Logging
import com.jalebi.driver.JobManager
import com.jalebi.exception._
import com.jalebi.hdfs.HDFSClient
import com.jalebi.hdfs.HDFSClient.RichHostPort
import com.jalebi.message.{InitializeExecutors, LoadDataset}
import com.typesafe.config.ConfigFactory
import org.apache.hadoop.net.NetUtils
import org.apache.hadoop.yarn.conf.YarnConfiguration

import scala.concurrent.duration._

case class JalebiContext private(conf: JalebiConfig) extends Logging {

  private val jobIdCounter = new AtomicLong(0)
  private val executorIdCounter = new AtomicLong(0)
  private val jobManager = JalebiContext.master.actorOf(JobManager.props(this), JobManager.name)
  implicit val timeout: Timeout = Timeout(10 seconds)
  jobManager ? InitializeExecutors

  @throws[DatasetNotFoundException]
  @throws[DatasetNotLoadedException]
  def loadDataset(name: String): Dataset = {
    jobManager ! LoadDataset(name)
    Dataset(name, jobManager)
  }

  def deleteDataset(name: String): Unit = {
    HDFSClient.withDistributedFileSystem(conf.hdfsHostPort, new YarnConfiguration()).deleteDataset(name)
  }

  @throws[DuplicateDatasetException]
  def createDataset(input: JalebiWriter): Unit = {
    val nodeMap = input.vertices.map(v => (v.id, v)).toMap
    val triplets = input.edges.map(edge => {
      if (!nodeMap.contains(edge.source.id)) {
        throw new InvalidVertexReferenceException(s"Node with id ${edge.source.id} not present in the list of nodes.")
      }
      if (!nodeMap.contains(edge.target.id)) {
        throw new InvalidVertexReferenceException(s"Node with id ${edge.target.id} not present in the list of nodes.")
      }
      Triplet(nodeMap(edge.source.id), edge, nodeMap(edge.target.id))
    }).grouped(conf.options.getPartitionSize().toInt)
      .map(Triplets(_))
    HDFSClient.withDistributedFileSystem(conf.hdfsHostPort, new YarnConfiguration()).createDataset(input.datasetName, triplets)
  }

  def close(): Unit = jobManager ! PoisonPill

  def onLocalMaster: Boolean = conf.master == "local"

  def newJobId(applicationId: String): String = s"$applicationId-Job-${jobIdCounter.getAndIncrement()}"

  def newExecutorId(applicationId: String): String = s"$applicationId-Executor-${executorIdCounter.getAndIncrement()}"
}

object JalebiContext {
  val master: ActorSystem = ActorSystem("Master", ConfigFactory.load("master"))

  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
