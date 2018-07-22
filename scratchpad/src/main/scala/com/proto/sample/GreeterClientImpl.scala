package com.proto.sample

import com.proto.generated.greeter.{GreeterGrpc, HelloRequest}
import io.grpc.ManagedChannelBuilder


object GreeterClientImpl {
  def main(args: Array[String]): Unit = {
    val channel = ManagedChannelBuilder
      .forAddress("127.0.0.1", GreeterServerImpl.PORT)
      .usePlaintext().build()

    val stub = GreeterGrpc.blockingStub(channel)
    val response = stub.sayHello(HelloRequest("MyName"))
    println(response.message)

    channel.shutdown()
  }
}
