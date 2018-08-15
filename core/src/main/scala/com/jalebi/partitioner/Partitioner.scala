package com.jalebi.partitioner

import com.jalebi.driver.ExecutorIdToParts
import com.jalebi.utils.Logging

trait Partitioner extends Logging {

  def partition(parts: Set[String], executors: Set[String]): ExecutorIdToParts

}
