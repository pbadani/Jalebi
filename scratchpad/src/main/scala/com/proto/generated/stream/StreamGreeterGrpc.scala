package com.proto.generated.stream

object StreamGreeterGrpc {
  val METHOD_SAY_HELLO: _root_.io.grpc.MethodDescriptor[com.proto.generated.stream.StreamHelloRequest, com.proto.generated.stream.StreamHelloResponse] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("com.proto.generated.StreamGreeter", "SayHello"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(new scalapb.grpc.Marshaller(com.proto.generated.stream.StreamHelloRequest))
      .setResponseMarshaller(new scalapb.grpc.Marshaller(com.proto.generated.stream.StreamHelloResponse))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("com.proto.generated.StreamGreeter")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(com.proto.generated.stream.StreamProto.javaDescriptor))
      .addMethod(METHOD_SAY_HELLO)
      .build()
  
  trait StreamGreeter extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = StreamGreeter
    def sayHello(responseObserver: _root_.io.grpc.stub.StreamObserver[com.proto.generated.stream.StreamHelloResponse]): _root_.io.grpc.stub.StreamObserver[com.proto.generated.stream.StreamHelloRequest]
  }
  
  object StreamGreeter extends _root_.scalapb.grpc.ServiceCompanion[StreamGreeter] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[StreamGreeter] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.proto.generated.stream.StreamProto.javaDescriptor.getServices().get(0)
  }
  
  trait StreamGreeterBlockingClient {
    def serviceCompanion = StreamGreeter
  }
  
  class StreamGreeterBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[StreamGreeterBlockingStub](channel, options) with StreamGreeterBlockingClient {
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): StreamGreeterBlockingStub = new StreamGreeterBlockingStub(channel, options)
  }
  
  class StreamGreeterStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[StreamGreeterStub](channel, options) with StreamGreeter {
    override def sayHello(responseObserver: _root_.io.grpc.stub.StreamObserver[com.proto.generated.stream.StreamHelloResponse]): _root_.io.grpc.stub.StreamObserver[com.proto.generated.stream.StreamHelloRequest] = {
      _root_.io.grpc.stub.ClientCalls.asyncBidiStreamingCall(channel.newCall(METHOD_SAY_HELLO, options), responseObserver)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): StreamGreeterStub = new StreamGreeterStub(channel, options)
  }
  
  def bindService(serviceImpl: StreamGreeter, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_SAY_HELLO,
      _root_.io.grpc.stub.ServerCalls.asyncBidiStreamingCall(new _root_.io.grpc.stub.ServerCalls.BidiStreamingMethod[com.proto.generated.stream.StreamHelloRequest, com.proto.generated.stream.StreamHelloResponse] {
        override def invoke(observer: _root_.io.grpc.stub.StreamObserver[com.proto.generated.stream.StreamHelloResponse]): _root_.io.grpc.stub.StreamObserver[com.proto.generated.stream.StreamHelloRequest] =
          serviceImpl.sayHello(observer)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): StreamGreeterBlockingStub = new StreamGreeterBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): StreamGreeterStub = new StreamGreeterStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.proto.generated.stream.StreamProto.javaDescriptor.getServices().get(0)
  
}