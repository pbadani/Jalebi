package com.jalebi.context

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.jalebi.message.FindNode

import scala.concurrent.duration._

case class Dataset(name: String, jobManager: ActorRef) {

  def findNode(nodeId: Long): String = {
    implicit val timeout = Timeout(5 seconds)
    jobManager.ask(FindNode(nodeId)).asInstanceOf[String]
  }

}
