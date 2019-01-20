package com.jalebi.context

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.jalebi.api.Node
import com.jalebi.common.Logging
import com.jalebi.message.FindNode

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class Dataset(name: String, jobManager: ActorRef) extends Logging {

  def findNode(nodeId: Long): Seq[Node] = {
    implicit val timeout = Timeout(10 seconds)
    Await.result(jobManager.ask(FindNode(nodeId)).asInstanceOf[Future[Seq[Node]]], 10 seconds)
  }
}
