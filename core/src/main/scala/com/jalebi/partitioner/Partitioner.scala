package com.jalebi.partitioner

import com.jalebi.common.Logging

trait Partitioner extends Logging {

  def partition(parts: Set[String], executors: Set[String]): Map[String, Set[String]]

}
