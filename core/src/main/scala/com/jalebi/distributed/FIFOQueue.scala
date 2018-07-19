package com.jalebi.distributed

class FIFOQueue[E] extends Queue[E] {
  override def enqueue(element: E): Unit = ???

  override def dequeue(): E = ???

  override def isEmpty: Boolean = ???

}

