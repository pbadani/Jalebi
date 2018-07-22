package com.jalebi.proto.generated.heartbeat

object HeartbeatGrpc {
  val METHOD_THUMP_THUMP: _root_.io.grpc.MethodDescriptor[com.jalebi.proto.generated.heartbeat.HeartbeatTrigger, com.jalebi.proto.generated.heartbeat.Beat] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("com.jalebi.proto.generated.Heartbeat", "ThumpThump"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.generated.heartbeat.HeartbeatTrigger))
      .setResponseMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.generated.heartbeat.Beat))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("com.jalebi.proto.generated.Heartbeat")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(com.jalebi.proto.generated.heartbeat.HeartbeatProto.javaDescriptor))
      .addMethod(METHOD_THUMP_THUMP)
      .build()
  
  trait Heartbeat extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = Heartbeat
    def thumpThump(request: com.jalebi.proto.generated.heartbeat.HeartbeatTrigger, responseObserver: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.heartbeat.Beat]): Unit
  }
  
  object Heartbeat extends _root_.scalapb.grpc.ServiceCompanion[Heartbeat] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[Heartbeat] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.jalebi.proto.generated.heartbeat.HeartbeatProto.javaDescriptor.getServices().get(0)
  }
  
  trait HeartbeatBlockingClient {
    def serviceCompanion = Heartbeat
    def thumpThump(request: com.jalebi.proto.generated.heartbeat.HeartbeatTrigger): scala.collection.Iterator[com.jalebi.proto.generated.heartbeat.Beat]
  }
  
  class HeartbeatBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[HeartbeatBlockingStub](channel, options) with HeartbeatBlockingClient {
    override def thumpThump(request: com.jalebi.proto.generated.heartbeat.HeartbeatTrigger): scala.collection.Iterator[com.jalebi.proto.generated.heartbeat.Beat] = {
      scala.collection.JavaConverters.asScalaIteratorConverter(_root_.io.grpc.stub.ClientCalls.blockingServerStreamingCall(channel.newCall(METHOD_THUMP_THUMP, options), request)).asScala
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): HeartbeatBlockingStub = new HeartbeatBlockingStub(channel, options)
  }
  
  class HeartbeatStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[HeartbeatStub](channel, options) with Heartbeat {
    override def thumpThump(request: com.jalebi.proto.generated.heartbeat.HeartbeatTrigger, responseObserver: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.heartbeat.Beat]): Unit = {
      _root_.io.grpc.stub.ClientCalls.asyncServerStreamingCall(channel.newCall(METHOD_THUMP_THUMP, options), request, responseObserver)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): HeartbeatStub = new HeartbeatStub(channel, options)
  }
  
  def bindService(serviceImpl: Heartbeat, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_THUMP_THUMP,
      _root_.io.grpc.stub.ServerCalls.asyncServerStreamingCall(new _root_.io.grpc.stub.ServerCalls.ServerStreamingMethod[com.jalebi.proto.generated.heartbeat.HeartbeatTrigger, com.jalebi.proto.generated.heartbeat.Beat] {
        override def invoke(request: com.jalebi.proto.generated.heartbeat.HeartbeatTrigger, observer: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.heartbeat.Beat]): Unit =
          serviceImpl.thumpThump(request, observer)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): HeartbeatBlockingStub = new HeartbeatBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): HeartbeatStub = new HeartbeatStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.jalebi.proto.generated.heartbeat.HeartbeatProto.javaDescriptor.getServices().get(0)
  
}