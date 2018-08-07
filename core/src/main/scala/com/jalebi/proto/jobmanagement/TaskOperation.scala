// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

sealed trait TaskOperation extends _root_.scalapb.GeneratedEnum {
  type EnumType = TaskOperation
  def isStartTask: _root_.scala.Boolean = false
  def isStopTask: _root_.scala.Boolean = false
  def isComputeNextBatch: _root_.scala.Boolean = false
  def companion: _root_.scalapb.GeneratedEnumCompanion[TaskOperation] = com.jalebi.proto.jobmanagement.TaskOperation
}

object TaskOperation extends _root_.scalapb.GeneratedEnumCompanion[TaskOperation] {
  implicit def enumCompanion: _root_.scalapb.GeneratedEnumCompanion[TaskOperation] = this
  @SerialVersionUID(0L)
  case object START_TASK extends TaskOperation {
    val value = 0
    val index = 0
    val name = "START_TASK"
    override def isStartTask: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object STOP_TASK extends TaskOperation {
    val value = 1
    val index = 1
    val name = "STOP_TASK"
    override def isStopTask: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  case object COMPUTE_NEXT_BATCH extends TaskOperation {
    val value = 2
    val index = 2
    val name = "COMPUTE_NEXT_BATCH"
    override def isComputeNextBatch: _root_.scala.Boolean = true
  }
  
  @SerialVersionUID(0L)
  final case class Unrecognized(value: _root_.scala.Int) extends TaskOperation with _root_.scalapb.UnrecognizedEnum
  
  lazy val values = scala.collection.Seq(START_TASK, STOP_TASK, COMPUTE_NEXT_BATCH)
  def fromValue(value: _root_.scala.Int): TaskOperation = value match {
    case 0 => START_TASK
    case 1 => STOP_TASK
    case 2 => COMPUTE_NEXT_BATCH
    case __other => Unrecognized(__other)
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.EnumDescriptor = JobmanagementProto.javaDescriptor.getEnumTypes.get(0)
  def scalaDescriptor: _root_.scalapb.descriptors.EnumDescriptor = JobmanagementProto.scalaDescriptor.enums(0)
}