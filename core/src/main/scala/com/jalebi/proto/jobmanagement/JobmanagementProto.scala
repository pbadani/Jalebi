// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

object JobmanagementProto extends _root_.scalapb.GeneratedFileObject {
  lazy val dependencies: Seq[_root_.scalapb.GeneratedFileObject] = Seq(
  )
  lazy val messagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq(
    com.jalebi.proto.jobmanagement.ExecutorRequest,
    com.jalebi.proto.jobmanagement.ExecutorResponse,
    com.jalebi.proto.jobmanagement.TaskRequest,
    com.jalebi.proto.jobmanagement.TaskResponse,
    com.jalebi.proto.jobmanagement.VertexResult,
    com.jalebi.proto.jobmanagement.EdgeResult,
    com.jalebi.proto.jobmanagement.HostPort
  )
  private lazy val ProtoBytes: Array[Byte] =
      scalapb.Encoding.fromBase64(scala.collection.Seq(
  """CiZzcmMvbWFpbi9yZXNvdXJjZXMvam9ibWFuYWdlbWVudC5wcm90bxIQY29tLmphbGViaS5wcm90byIxCg9FeGVjdXRvclJlc
  XVlc3QSHgoKZXhlY3V0b3JJZBgBIAEoCVIKZXhlY3V0b3JJZCJ4ChBFeGVjdXRvclJlc3BvbnNlEiwKEWhlYXJ0YmVhdEludGVyd
  mFsGAEgASgDUhFoZWFydGJlYXRJbnRlcnZhbBI2Cghob3N0cG9ydBgCIAEoCzIaLmNvbS5qYWxlYmkucHJvdG8uSG9zdFBvcnRSC
  Ghvc3Rwb3J0It8BCgtUYXNrUmVxdWVzdBIUCgVqb2JJZBgBIAEoCVIFam9iSWQSFgoGdGFza0lkGAIgASgJUgZ0YXNrSWQSNgoId
  GFza1R5cGUYAyABKA4yGi5jb20uamFsZWJpLnByb3RvLlRhc2tUeXBlUgh0YXNrVHlwZRIkCg1zdGFydFZlcnRleElkGAQgASgDU
  g1zdGFydFZlcnRleElkEhgKB2RhdGFzZXQYBSABKAlSB2RhdGFzZXQSFAoFcGFydHMYBiADKAlSBXBhcnRzEhQKBWxpbWl0GAcgA
  SgDUgVsaW1pdCKoAwoMVGFza1Jlc3BvbnNlEhQKBWpvYklkGAEgASgJUgVqb2JJZBIWCgZ0YXNrSWQYAiABKAlSBnRhc2tJZBI5C
  gl0YXNrU3RhdGUYAyABKA4yGy5jb20uamFsZWJpLnByb3RvLlRhc2tTdGF0ZVIJdGFza1N0YXRlEh4KCmV4ZWN1dG9ySWQYBCABK
  AlSCmV4ZWN1dG9ySWQSRQoNZXhlY3V0b3JTdGF0ZRgFIAEoDjIfLmNvbS5qYWxlYmkucHJvdG8uRXhlY3V0b3JTdGF0ZVINZXhlY
  3V0b3JTdGF0ZRJCCgxkYXRhc2V0U3RhdGUYBiABKA4yHi5jb20uamFsZWJpLnByb3RvLkRhdGFzZXRTdGF0ZVIMZGF0YXNldFN0Y
  XRlEkQKDXZlcnRleFJlc3VsdHMYByADKAsyHi5jb20uamFsZWJpLnByb3RvLlZlcnRleFJlc3VsdFINdmVydGV4UmVzdWx0cxI+C
  gtlZGdlUmVzdWx0cxgIIAMoCzIcLmNvbS5qYWxlYmkucHJvdG8uRWRnZVJlc3VsdFILZWRnZVJlc3VsdHMioQEKDFZlcnRleFJlc
  3VsdBIaCgh2ZXJ0ZXhJZBgBIAEoA1IIdmVydGV4SWQSPAoEZGF0YRgCIAMoCzIoLmNvbS5qYWxlYmkucHJvdG8uVmVydGV4UmVzd
  Wx0LkRhdGFFbnRyeVIEZGF0YRo3CglEYXRhRW50cnkSEAoDa2V5GAEgASgJUgNrZXkSFAoFdmFsdWUYAiABKAlSBXZhbHVlOgI4A
  SLRAQoKRWRnZVJlc3VsdBImCg5zb3VyY2VWZXJ0ZXhJZBgBIAEoA1IOc291cmNlVmVydGV4SWQSJgoOdGFyZ2V0VmVydGV4SWQYA
  iABKANSDnRhcmdldFZlcnRleElkEjoKBGRhdGEYAyADKAsyJi5jb20uamFsZWJpLnByb3RvLkVkZ2VSZXN1bHQuRGF0YUVudHJ5U
  gRkYXRhGjcKCURhdGFFbnRyeRIQCgNrZXkYASABKAlSA2tleRIUCgV2YWx1ZRgCIAEoCVIFdmFsdWU6AjgBIkoKCEhvc3RQb3J0E
  hYKBnNjaGVtZRgBIAEoCVIGc2NoZW1lEhIKBGhvc3QYAiABKAlSBGhvc3QSEgoEcG9ydBgDIAEoA1IEcG9ydCp6Cg1FeGVjdXRvc
  lN0YXRlEgcKA05FVxAAEg0KCUFMTE9DQVRFRBABEg4KClJFR0lTVEVSRUQQAhIPCgtSVU5OSU5HX0pPQhADEgwKCFJVTk5BQkxFE
  AQSEAoMVU5SRUdJU1RFUkVEEAUSEAoMQVNTVU1FRF9ERUFEEAYqMQoMRGF0YXNldFN0YXRlEggKBE5PTkUQABILCgdMT0FESU5HE
  AESCgoGTE9BREVEEAIqMwoJVGFza1N0YXRlEgsKB1JVTk5JTkcQABINCglDT01QTEVURUQQARIKCgZGQUlMRUQQAipTCghUYXNrV
  HlwZRIQCgxMT0FEX0RBVEFTRVQQABIRCg1TRUFSQ0hfVkVSVEVYEAESEQoNQlJFQURUSF9GSVJTVBACEg8KC0RFUFRIX0ZJUlNUE
  AMypQIKFUpvYk1hbmFnZW1lbnRQcm90b2NvbBJbChByZWdpc3RlckV4ZWN1dG9yEiEuY29tLmphbGViaS5wcm90by5FeGVjdXRvc
  lJlcXVlc3QaIi5jb20uamFsZWJpLnByb3RvLkV4ZWN1dG9yUmVzcG9uc2UiABJdChJ1bnJlZ2lzdGVyRXhlY3V0b3ISIS5jb20ua
  mFsZWJpLnByb3RvLkV4ZWN1dG9yUmVxdWVzdBoiLmNvbS5qYWxlYmkucHJvdG8uRXhlY3V0b3JSZXNwb25zZSIAElAKCXN0YXJ0V
  GFsaxIeLmNvbS5qYWxlYmkucHJvdG8uVGFza1Jlc3BvbnNlGh0uY29tLmphbGViaS5wcm90by5UYXNrUmVxdWVzdCIAKAEwAWIGc
  HJvdG8z"""
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