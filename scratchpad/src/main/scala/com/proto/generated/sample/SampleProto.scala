// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.proto.generated.sample

object SampleProto extends _root_.scalapb.GeneratedFileObject {
  lazy val dependencies: Seq[_root_.scalapb.GeneratedFileObject] = Seq(
  )
  lazy val messagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq(
    com.proto.generated.sample.Person
  )
  private lazy val ProtoBytes: Array[Byte] =
      scalapb.Encoding.fromBase64(scala.collection.Seq(
  """Ch9zcmMvbWFpbi9yZXNvdXJjZXMvc2FtcGxlLnByb3RvEhNjb20ucHJvdG8uZ2VuZXJhdGVkIpACCgZQZXJzb24SEgoEbmFtZ
  RgBIAEoCVIEbmFtZRIOCgJpZBgCIAEoBVICaWQSFAoFZW1haWwYAyABKAlSBWVtYWlsEj0KBXBob25lGAQgAygLMicuY29tLnByb
  3RvLmdlbmVyYXRlZC5QZXJzb24uUGhvbmVOdW1iZXJSBXBob25lGmAKC1Bob25lTnVtYmVyEhYKBm51bWJlchgBIAEoCVIGbnVtY
  mVyEjkKBHR5cGUYAiABKA4yJS5jb20ucHJvdG8uZ2VuZXJhdGVkLlBlcnNvbi5QaG9uZVR5cGVSBHR5cGUiKwoJUGhvbmVUeXBlE
  goKBk1PQklMRRAAEggKBEhPTUUQARIICgRXT1JLEAJiBnByb3RvMw=="""
      ).mkString)
  lazy val scalaDescriptor: _root_.scalapb.descriptors.FileDescriptor = {
    val scalaProto = com.google.protobuf.descriptor.FileDescriptorProto.parseFrom(ProtoBytes)
    _root_.scalapb.descriptors.FileDescriptor.buildFrom(scalaProto, dependencies.map(_.scalaDescriptor))
  }
  lazy val javaDescriptor: com.google.protobuf.Descriptors.FileDescriptor = {
    val javaProto = com.google.protobuf.DescriptorProtos.FileDescriptorProto.parseFrom(ProtoBytes)
    com.google.protobuf.Descriptors.FileDescriptor.buildFrom(javaProto, Array(
    ))
  }
  @deprecated("Use javaDescriptor instead. In a future version this will refer to scalaDescriptor.", "ScalaPB 0.5.47")
  def descriptor: com.google.protobuf.Descriptors.FileDescriptor = javaDescriptor
}