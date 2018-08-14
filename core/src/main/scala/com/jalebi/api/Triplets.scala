package com.jalebi.api

case class Triplets[V, E](values: Seq[Triplet[V, E]])

object Triplets {
  def apply[V, E](values: Seq[Triplet[V, E]]): Triplets[V, E] = new Triplets(values)
}