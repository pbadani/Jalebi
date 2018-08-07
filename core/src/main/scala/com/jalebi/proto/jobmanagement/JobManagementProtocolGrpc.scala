package com.jalebi.proto.jobmanagement

object JobManagementProtocolGrpc {
  val METHOD_REGISTER_EXECUTOR: _root_.io.grpc.MethodDescriptor[com.jalebi.proto.jobmanagement.RegisterExecutorRequest, com.jalebi.proto.jobmanagement.RegisterExecutorResponse] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("com.jalebi.proto.JobManagementProtocol", "registerExecutor"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.jobmanagement.RegisterExecutorRequest))
      .setResponseMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.jobmanagement.RegisterExecutorResponse))
      .build()
  
  val METHOD_START_TALK: _root_.io.grpc.MethodDescriptor[com.jalebi.proto.jobmanagement.TaskRequest, com.jalebi.proto.jobmanagement.TaskResponse] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("com.jalebi.proto.JobManagementProtocol", "startTalk"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.jobmanagement.TaskRequest))
      .setResponseMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.jobmanagement.TaskResponse))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("com.jalebi.proto.JobManagementProtocol")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(com.jalebi.proto.jobmanagement.JobmanagementProto.javaDescriptor))
      .addMethod(METHOD_REGISTER_EXECUTOR)
      .addMethod(METHOD_START_TALK)
      .build()
  
  trait JobManagementProtocol extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = JobManagementProtocol
    def registerExecutor(request: com.jalebi.proto.jobmanagement.RegisterExecutorRequest): scala.concurrent.Future[com.jalebi.proto.jobmanagement.RegisterExecutorResponse]
    def startTalk(responseObserver: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.TaskResponse]): _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.TaskRequest]
  }
  
  object JobManagementProtocol extends _root_.scalapb.grpc.ServiceCompanion[JobManagementProtocol] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[JobManagementProtocol] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.jalebi.proto.jobmanagement.JobmanagementProto.javaDescriptor.getServices().get(0)
  }
  
  trait JobManagementProtocolBlockingClient {
    def serviceCompanion = JobManagementProtocol
    def registerExecutor(request: com.jalebi.proto.jobmanagement.RegisterExecutorRequest): com.jalebi.proto.jobmanagement.RegisterExecutorResponse
  }
  
  class JobManagementProtocolBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[JobManagementProtocolBlockingStub](channel, options) with JobManagementProtocolBlockingClient {
    override def registerExecutor(request: com.jalebi.proto.jobmanagement.RegisterExecutorRequest): com.jalebi.proto.jobmanagement.RegisterExecutorResponse = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_REGISTER_EXECUTOR, options), request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): JobManagementProtocolBlockingStub = new JobManagementProtocolBlockingStub(channel, options)
  }
  
  class JobManagementProtocolStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[JobManagementProtocolStub](channel, options) with JobManagementProtocol {
    override def registerExecutor(request: com.jalebi.proto.jobmanagement.RegisterExecutorRequest): scala.concurrent.Future[com.jalebi.proto.jobmanagement.RegisterExecutorResponse] = {
      scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_REGISTER_EXECUTOR, options), request))
    }
    
    override def startTalk(responseObserver: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.TaskResponse]): _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.TaskRequest] = {
      _root_.io.grpc.stub.ClientCalls.asyncBidiStreamingCall(channel.newCall(METHOD_START_TALK, options), responseObserver)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): JobManagementProtocolStub = new JobManagementProtocolStub(channel, options)
  }
  
  def bindService(serviceImpl: JobManagementProtocol, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_REGISTER_EXECUTOR,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[com.jalebi.proto.jobmanagement.RegisterExecutorRequest, com.jalebi.proto.jobmanagement.RegisterExecutorResponse] {
        override def invoke(request: com.jalebi.proto.jobmanagement.RegisterExecutorRequest, observer: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.RegisterExecutorResponse]): Unit =
          serviceImpl.registerExecutor(request).onComplete(scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .addMethod(
      METHOD_START_TALK,
      _root_.io.grpc.stub.ServerCalls.asyncBidiStreamingCall(new _root_.io.grpc.stub.ServerCalls.BidiStreamingMethod[com.jalebi.proto.jobmanagement.TaskRequest, com.jalebi.proto.jobmanagement.TaskResponse] {
        override def invoke(observer: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.TaskResponse]): _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.jobmanagement.TaskRequest] =
          serviceImpl.startTalk(observer)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): JobManagementProtocolBlockingStub = new JobManagementProtocolBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): JobManagementProtocolStub = new JobManagementProtocolStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.jalebi.proto.jobmanagement.JobmanagementProto.javaDescriptor.getServices().get(0)
  
}