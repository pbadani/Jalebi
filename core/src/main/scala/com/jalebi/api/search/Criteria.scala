package com.jalebi.api.search

trait Criteria[-T] {
  def matches(value: T): Boolean
}

object MatchAll extends Criteria[Any] {
  override def matches(value: Any): Boolean = true
}

object Criteria {
  def createNew[T](f: T => Boolean): Criteria[T] = f(_)

  def matchAll[T](): Criteria[T] = MatchAll
}

object UnaryCriteria {
  def negate[T](criteria: Criteria[T]): Criteria[T] = !criteria.matches(_)
}

object BinaryCriteria {
  def and[T](criteria1: Criteria[T], criteria2: Criteria[T]): Criteria[T] = {
    (value: T) => criteria1.matches(value) && criteria2.matches(value)
  }

  def or[T](criteria1: Criteria[T], criteria2: Criteria[T]): Criteria[T] = {
    (value: T) => criteria1.matches(value) || criteria2.matches(value)
  }
}