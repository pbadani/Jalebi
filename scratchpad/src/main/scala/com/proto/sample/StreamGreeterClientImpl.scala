package com.proto.sample

import java.util.concurrent.TimeUnit

import com.proto.generated.stream.{StreamGreeterGrpc, StreamHelloRequest, StreamHelloResponse}
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch

object StreamGreeterClientImpl {
  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress("127.0.0.1", StreamGreeterServerImpl.PORT)
      .usePlaintext().build()

    val stub = StreamGreeterGrpc.stub(channel)
    var resp: List[StreamHelloResponse] = Nil

    val finishLatch = new CountDownLatch(10)
    val clientStream = stub.sayHello(new StreamObserver[StreamHelloResponse] {
      override def onError(t: Throwable): Unit = {
        t.printStackTrace()
      }

      override def onCompleted(): Unit = {
        finishLatch.countDown()
        println("Done")
      }

      override def onNext(value: StreamHelloResponse): Unit = {
        resp = value :: resp
        println(s"Response from Server -> ${value.message}")
      }
    })

    for (i <- 1 to 10) {
      clientStream.onNext(StreamHelloRequest(s"Hi $i"))
    }
    clientStream.onCompleted()
    finishLatch.await(1000, TimeUnit.MINUTES)
    for (r <- resp) {
      println(r.message)
    }

  }
}
