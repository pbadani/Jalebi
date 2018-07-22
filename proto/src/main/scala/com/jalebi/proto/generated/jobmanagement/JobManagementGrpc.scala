package com.jalebi.proto.generated.jobmanagement

object JobManagementGrpc {
  val METHOD_START_TASK: _root_.io.grpc.MethodDescriptor[com.jalebi.proto.generated.jobmanagement.TaskRequest, com.jalebi.proto.generated.jobmanagement.TaskResponse] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("com.jalebi.proto.generated.JobManagement", "startTask"))
      .setSampledToLocalTracing(true)
      .setRequestMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.generated.jobmanagement.TaskRequest))
      .setResponseMarshaller(new scalapb.grpc.Marshaller(com.jalebi.proto.generated.jobmanagement.TaskResponse))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("com.jalebi.proto.generated.JobManagement")
      .setSchemaDescriptor(new _root_.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(com.jalebi.proto.generated.jobmanagement.JobmanagementProto.javaDescriptor))
      .addMethod(METHOD_START_TASK)
      .build()
  
  trait JobManagement extends _root_.scalapb.grpc.AbstractService {
    override def serviceCompanion = JobManagement
    def startTask(responseObserver: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.jobmanagement.TaskResponse]): _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.jobmanagement.TaskRequest]
  }
  
  object JobManagement extends _root_.scalapb.grpc.ServiceCompanion[JobManagement] {
    implicit def serviceCompanion: _root_.scalapb.grpc.ServiceCompanion[JobManagement] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.jalebi.proto.generated.jobmanagement.JobmanagementProto.javaDescriptor.getServices().get(0)
  }
  
  trait JobManagementBlockingClient {
    def serviceCompanion = JobManagement
  }
  
  class JobManagementBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[JobManagementBlockingStub](channel, options) with JobManagementBlockingClient {
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): JobManagementBlockingStub = new JobManagementBlockingStub(channel, options)
  }
  
  class JobManagementStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[JobManagementStub](channel, options) with JobManagement {
    override def startTask(responseObserver: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.jobmanagement.TaskResponse]): _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.jobmanagement.TaskRequest] = {
      _root_.io.grpc.stub.ClientCalls.asyncBidiStreamingCall(channel.newCall(METHOD_START_TASK, options), responseObserver)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): JobManagementStub = new JobManagementStub(channel, options)
  }
  
  def bindService(serviceImpl: JobManagement, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_START_TASK,
      _root_.io.grpc.stub.ServerCalls.asyncBidiStreamingCall(new _root_.io.grpc.stub.ServerCalls.BidiStreamingMethod[com.jalebi.proto.generated.jobmanagement.TaskRequest, com.jalebi.proto.generated.jobmanagement.TaskResponse] {
        override def invoke(observer: _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.jobmanagement.TaskResponse]): _root_.io.grpc.stub.StreamObserver[com.jalebi.proto.generated.jobmanagement.TaskRequest] =
          serviceImpl.startTask(observer)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): JobManagementBlockingStub = new JobManagementBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): JobManagementStub = new JobManagementStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = com.jalebi.proto.generated.jobmanagement.JobmanagementProto.javaDescriptor.getServices().get(0)
  
}