package com.jalebi.api

abstract class AbstractComputationResult {

  def limit(limit: Long): Unit
  def collect(): Unit
}
