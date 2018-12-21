package com.akka

import akka.actor.{Actor, ActorSystem, Props}

object First {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    val a = system.actorOf(TestProducer.props("a"))
    a ! Message1("aaaa")
  }
}

case class Message1(s: String)

case class TestProducer(i: String) extends Actor {
  override def receive: Receive = {
    case Message1(s) => println(s)
  }
}

object TestProducer {
  def props(i: String) = Props(new TestProducer(i))

  def apply(i: String): TestProducer = new TestProducer(i)
}
