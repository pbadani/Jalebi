package com.jalebi.api.search

trait MatchCriteria[-T] {
  def matches(value: T): Boolean
}

object MatchAll extends MatchCriteria[Any] {
  override def matches(value: Any): Boolean = true
}

object MatchCriteria {
  def createNew[T](f: T => Boolean): MatchCriteria[T] = f(_)

  def matchAll[T](): MatchCriteria[T] = MatchAll
}

object UnaryCriteria {
  def negate[T](c: MatchCriteria[T]): MatchCriteria[T] = !c.matches(_)
}

object BinaryCriteria {
  def and[T](c1: MatchCriteria[T], c2: MatchCriteria[T]): MatchCriteria[T] = {
    (value: T) => c1.matches(value) && c2.matches(value)
  }

  def or[T](c1: MatchCriteria[T], c2: MatchCriteria[T]): MatchCriteria[T] = {
    (value: T) => c1.matches(value) || c2.matches(value)
  }
}

case class LimitCriteria private(limit: Long) {
  def isReached(currentVal: Long): Boolean = limit <= currentVal
}

object LimitCriteria {
  def createNew(limit: Long) = new LimitCriteria(limit)

  def matchAll() = new LimitCriteria(Long.MaxValue)
}