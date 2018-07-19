package com.jalebi.distributed

abstract class Queue[E] {
//  self: Bound =>

  def enqueue(element: E)

  def dequeue(): E

  def isEmpty: Boolean
}
