package com.jalebi.api

trait EdgeWeight {
  def weight: Long
}

trait UnitWeight extends EdgeWeight {
  override def weight: Long = 1
}

trait Weighted extends EdgeWeight