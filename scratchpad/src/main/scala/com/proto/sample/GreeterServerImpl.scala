package com.proto.sample

import com.proto.generated.greeter.{GreeterGrpc, HelloRequest, HelloResponse}
import io.grpc.ServerBuilder

import scala.concurrent.{ExecutionContext, Future}

class GreeterServerImpl extends GreeterGrpc.Greeter {
  override def sayHello(request: HelloRequest): Future[HelloResponse] = {
    Future.successful(HelloResponse(s"Hello ${request.name}!"))
  }
}

object GreeterServerImpl {
  val PORT = 8080

  def main(args: Array[String]): Unit = {
    val server = ServerBuilder
      .forPort(PORT)
      .addService(GreeterGrpc.bindService(new GreeterServerImpl(), ExecutionContext.global))
      .build()
      .start()

    println(s"Started Listening on port: $PORT")

    server.awaitTermination()

    sys.addShutdownHook({
      println("Shutting down the server")
      server.shutdown()
    })
  }
}