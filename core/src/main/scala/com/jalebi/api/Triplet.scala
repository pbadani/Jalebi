package com.jalebi.api

case class Triplet(source: Node, edge: Edge, target: Node)

object Triplet {
  def apply(source: Node, edge: Edge, target: Node): Triplet = new Triplet(source, edge, target)
}
