// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

sealed trait DatasetState extends _root_.scalapb.GeneratedEnum {
  type EnumType = DatasetState
  def isNone: _root_.scala.Boolean = false
  def isLoading: _root_.scala.Boolean = false
  def isLoaded: _root_.scala.Boolean = false
  def companion: _root_.scalapb.GeneratedEnumCompanion[DatasetState] = com.jalebi.proto.jobmanagement.DatasetState
}

object DatasetState extends _root_.scalapb.GeneratedEnumCompanion[DatasetState] {
  implicit def enumCompanion: _root_.scalapb.GeneratedEnumCompanion[DatasetState] = this
  @SerialVersionUID(0L)
  case object NONE extends DatasetState {
    val value = 0
    val index = 0
    val name = "NONE"
    override def isNone: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object LOADING extends DatasetState {
    val value = 1
    val index = 1
    val name = "LOADING"
    override def isLoading: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object LOADED extends DatasetState {
    val value = 2
    val index = 2
    val name = "LOADED"
    override def isLoaded: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  final case class Unrecognized(value: _root_.scala.Int) extends DatasetState with _root_.scalapb.UnrecognizedEnum
  
  lazy val values = scala.collection.Seq(NONE, LOADING, LOADED)
  def fromValue(value: _root_.scala.Int): DatasetState = value match {
    case 0 => NONE
    case 1 => LOADING
    case 2 => LOADED
    case __other => Unrecognized(__other)
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.EnumDescriptor = JobmanagementProto.javaDescriptor.getEnumTypes.get(1)
  def scalaDescriptor: _root_.scalapb.descriptors.EnumDescriptor = JobmanagementProto.scalaDescriptor.enums(1)
}