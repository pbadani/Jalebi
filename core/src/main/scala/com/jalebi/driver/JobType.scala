package com.jalebi.job

object JobType extends Enumeration {
  type JT = Value
  val BREADTH_FIRST, DEPTH_FIRST, TOPOLOGICAL_SORT = Value
}
