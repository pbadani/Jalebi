package com.proto.generated.greeter

object GreeterGrpc {
  val METHOD_SAY_HELLO: _root_.io.grpc.MethodDescriptor[com.proto.generated.greeter.HelloRequest, com.proto.generated.greeter.HelloResponse] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("com.proto.generated.Greeter", "SayHello"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(new scalapb.grpc.Marshaller(com.proto.generated.greeter.HelloRequest))
      .setResponseMarshaller(new scalapb.grpc.Marshaller(com.proto.generated.greeter.HelloResponse))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("com.proto.generated.Greeter")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(com.proto.generated.greeter.GreeterProto.javaDescriptor))
      .addMethod(METHOD_SAY_HELLO)
      .build()
  
  trait Greeter extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = Greeter
    def sayHello(request: com.proto.generated.greeter.HelloRequest): scala.concurrent.Future[com.proto.generated.greeter.HelloResponse]
  }
  
  object Greeter extends _root_.scalapb.grpc.ServiceCompanion[Greeter] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[Greeter] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.proto.generated.greeter.GreeterProto.javaDescriptor.getServices().get(0)
  }
  
  trait GreeterBlockingClient {
    def serviceCompanion = Greeter
    def sayHello(request: com.proto.generated.greeter.HelloRequest): com.proto.generated.greeter.HelloResponse
  }
  
  class GreeterBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[GreeterBlockingStub](channel, options) with GreeterBlockingClient {
    override def sayHello(request: com.proto.generated.greeter.HelloRequest): com.proto.generated.greeter.HelloResponse = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_SAY_HELLO, options), request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): GreeterBlockingStub = new GreeterBlockingStub(channel, options)
  }
  
  class GreeterStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[GreeterStub](channel, options) with Greeter {
    override def sayHello(request: com.proto.generated.greeter.HelloRequest): scala.concurrent.Future[com.proto.generated.greeter.HelloResponse] = {
      scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_SAY_HELLO, options), request))
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): GreeterStub = new GreeterStub(channel, options)
  }
  
  def bindService(serviceImpl: Greeter, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_SAY_HELLO,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[com.proto.generated.greeter.HelloRequest, com.proto.generated.greeter.HelloResponse] {
        override def invoke(request: com.proto.generated.greeter.HelloRequest, observer: _root_.io.grpc.stub.StreamObserver[com.proto.generated.greeter.HelloResponse]): Unit =
          serviceImpl.sayHello(request).onComplete(scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): GreeterBlockingStub = new GreeterBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): GreeterStub = new GreeterStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.proto.generated.greeter.GreeterProto.javaDescriptor.getServices().get(0)
  
}