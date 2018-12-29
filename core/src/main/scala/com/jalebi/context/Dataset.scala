package com.jalebi.context

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout
import com.jalebi.api.VertexID
import com.jalebi.message.FindVertex

import scala.concurrent.duration._

case class Dataset(name: String, jobManager: ActorRef) {

  def findVertex(vertexId: VertexID): String = {
    implicit val timeout = Timeout(5 seconds)
    jobManager.ask(FindVertex(vertexId)).asInstanceOf[String]
  }

}
