package com.jalebi.distributed

trait Bound {
  def getBound: Long
}

trait FixedBound extends Bound {
  def getBound: Long
}

trait NoBound extends Bound {
  override def getBound: Long = Long.MaxValue
}
