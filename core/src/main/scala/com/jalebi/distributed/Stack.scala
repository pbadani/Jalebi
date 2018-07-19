package com.jalebi.distributed

abstract class Stack[E] {
//  self: Bound =>

  def push(element: E)

  def pop(): E

  def isEmpty: Boolean
}
