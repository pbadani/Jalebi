package com.jalebi.context

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.jalebi.api.Node
import com.jalebi.message.FindNode

import scala.concurrent.Future
import scala.concurrent.duration._

case class Dataset(name: String, jobManager: ActorRef) {

  def findNode(nodeId: Long): Future[Set[Node]] = {
    implicit val timeout = Timeout(10 seconds)
    jobManager.ask(FindNode(nodeId)).asInstanceOf[Future[Set[Node]]]
  }

}
