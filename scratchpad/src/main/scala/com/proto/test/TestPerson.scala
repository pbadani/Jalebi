package com.proto.test

import com.proto.generated.person.Person

object TestPerson {
  def main(args: Array[String]): Unit = {
    println(Person("a", 0))
  }
}
