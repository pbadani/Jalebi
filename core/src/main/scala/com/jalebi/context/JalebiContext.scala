package com.jalebi.context

import java.util.concurrent.atomic.AtomicLong

import com.jalebi.api._
import com.jalebi.exception.{DatasetNotFoundException, DatasetNotLoadedException}
import com.jalebi.hdfs.{FileSystemType, HDFSClient}
import com.jalebi.job.JobManager

case class JalebiContext private(conf: JalebiConfig) {

  val applicationID = s"Jalebi_App_${System.currentTimeMillis()}"
  val jobIDCounter = new AtomicLong(0)
  var currentDataset: Option[String] = None
  val jobManager: JobManager = JobManager.createNew(this)

  private def validate[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = {
    //    val vertexID
    null
  }

  def load[V, E](vertices: Vertices[V], edges: Edges[E]): Jalebi[V, E] = {
    validate(vertices, edges)
    //    Jalebi.createLocal(this, vertices, edges)
    null
  }

  @throws[DatasetNotFoundException]
  @throws[DatasetNotLoadedException]
  def load[V, E](name: String, fileSystem: FileSystemType.FS): Unit = {
    val hdfsClient = fileSystem match {
      case FileSystemType.HDFS_LOCAL => HDFSClient.withLocalFileSystem()
      case FileSystemType.HDFS_DISTRIBUTED => HDFSClient.withDistributedFileSystem(conf.hdfsHostPort)
    }
    if (!hdfsClient.checkDatasetExists(name)) {
      throw new DatasetNotFoundException(s"Dataset $name not found at $fileSystem filesystem.")
    }
    if (jobManager.load(hdfsClient, name)) {
      currentDataset = Some(name)
    }
  }

  def generateNewJobID(): Long = jobIDCounter.getAndIncrement()
}

object JalebiContext {
  def apply(conf: JalebiConfig): JalebiContext = new JalebiContext(conf)
}
