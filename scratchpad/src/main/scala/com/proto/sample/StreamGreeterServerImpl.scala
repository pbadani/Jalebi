package com.proto.sample

import com.proto.generated.stream.{StreamGreeterGrpc, StreamHelloRequest, StreamHelloResponse}
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver

import scala.concurrent.ExecutionContext

class StreamGreeterServerImpl extends StreamGreeterGrpc.StreamGreeter {
  override def sayHello(responseObserver: StreamObserver[StreamHelloResponse]): StreamObserver[StreamHelloRequest] = {
    new StreamObserver[StreamHelloRequest] {
      override def onError(t: Throwable): Unit = {
        t.printStackTrace()
      }

      override def onCompleted(): Unit = {
        println("Server completed")
        responseObserver.onCompleted()
      }

      override def onNext(value: StreamHelloRequest): Unit = {
        println(s"At Server - ${value.name}")
        responseObserver.onNext(StreamHelloResponse(s"Hello from Response: ${value.name}"))
      }
    }
  }
}

object StreamGreeterServerImpl {
  val PORT = 8081

  def main(args: Array[String]): Unit = {
    val server = ServerBuilder
      .forPort(PORT)
      .addService(StreamGreeterGrpc.bindService(new StreamGreeterServerImpl(), ExecutionContext.global))
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
