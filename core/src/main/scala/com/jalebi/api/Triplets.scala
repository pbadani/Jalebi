package com.jalebi.api

case class Triplets(values: Seq[Triplet])

object Triplets {
  def apply(values: Seq[Triplet]): Triplets = new Triplets(values)
}