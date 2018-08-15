package com.jalebi.partitioner

import com.jalebi.executor.ExecutorIdToParts

object HashPartitioner extends Partitioner {

  override def partition(parts: Set[String], executors: Set[String]): ExecutorIdToParts = {
    val minimumExecutors = Math.min(parts.size, executors.size)
    val partsPerExecutor = Math.ceil(parts.size.toDouble / minimumExecutors)
    val mapping = executors.toIterator.zip(parts.grouped(partsPerExecutor.toInt)).toMap
    LOGGER.info(s"Assigning partitions to Executors: ${
      mapping.map {
        case (executorId, partitions) => s"ExecutorId $executorId   - Parts ${partitions.mkString(",")}"
      }
    }")
    ExecutorIdToParts(mapping)
  }
}
