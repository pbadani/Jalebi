package com.jalebi.partitioner

object HashPartitioner extends Partitioner {

  override def partition(parts: Set[String], executors: Set[String]): Map[String, Set[String]] = {
    val partsPerExecutor = Math.ceil(parts.size.toDouble / Math.min(parts.size, executors.size))
    val mapping = executors.toIterator.zip(parts.grouped(partsPerExecutor.toInt)).toMap
    LOGGER.info(s"Assigning partitions to Executors: ${
      mapping.map {
        case (executorId, partitions) => s"$executorId - [${partitions.mkString(",")}]"
      }.mkString(", ")
    }")
    mapping
  }
}
