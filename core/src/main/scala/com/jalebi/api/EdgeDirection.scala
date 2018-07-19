package com.jalebi.api

trait EdgeDirection {
  def isDirected: Boolean
}

trait Directed extends EdgeDirection {
  def isDirected = true
}

trait Undirected extends EdgeDirection {
  def isDirected = false
}
