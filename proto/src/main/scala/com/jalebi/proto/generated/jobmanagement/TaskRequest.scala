// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.generated.jobmanagement

@SerialVersionUID(0L)
final case class TaskRequest(
    jobID: _root_.scala.Predef.String = "",
    op: com.jalebi.proto.generated.jobmanagement.TaskOperation = com.jalebi.proto.generated.jobmanagement.TaskOperation.START
    ) extends scalapb.GeneratedMessage with scalapb.Message[TaskRequest] with scalapb.lenses.Updatable[TaskRequest] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (jobID != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, jobID) }
      if (op != com.jalebi.proto.generated.jobmanagement.TaskOperation.START) { __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(2, op.value) }
      __size
    }
    final override def serializedSize: _root_.scala.Int = {
      var read = __serializedSizeCachedValue
      if (read == 0) {
        read = __computeSerializedValue()
        __serializedSizeCachedValue = read
      }
      read
    }
    def writeTo(`_output__`: _root_.com.google.protobuf.CodedOutputStream): _root_.scala.Unit = {
      {
        val __v = jobID
        if (__v != "") {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = op
        if (__v != com.jalebi.proto.generated.jobmanagement.TaskOperation.START) {
          _output__.writeEnum(2, __v.value)
        }
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.jalebi.proto.generated.jobmanagement.TaskRequest = {
      var __jobID = this.jobID
      var __op = this.op
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __jobID = _input__.readString()
          case 16 =>
            __op = com.jalebi.proto.generated.jobmanagement.TaskOperation.fromValue(_input__.readEnum())
          case tag => _input__.skipField(tag)
        }
      }
      com.jalebi.proto.generated.jobmanagement.TaskRequest(
          jobID = __jobID,
          op = __op
      )
    }
    def withJobID(__v: _root_.scala.Predef.String): TaskRequest = copy(jobID = __v)
    def withOp(__v: com.jalebi.proto.generated.jobmanagement.TaskOperation): TaskRequest = copy(op = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = jobID
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = op.javaValueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(jobID)
        case 2 => _root_.scalapb.descriptors.PEnum(op.scalaValueDescriptor)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.jalebi.proto.generated.jobmanagement.TaskRequest
}

object TaskRequest extends scalapb.GeneratedMessageCompanion[com.jalebi.proto.generated.jobmanagement.TaskRequest] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.jalebi.proto.generated.jobmanagement.TaskRequest] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.jalebi.proto.generated.jobmanagement.TaskRequest = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.jalebi.proto.generated.jobmanagement.TaskRequest(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
      com.jalebi.proto.generated.jobmanagement.TaskOperation.fromValue(__fieldsMap.getOrElse(__fields.get(1), com.jalebi.proto.generated.jobmanagement.TaskOperation.START.javaValueDescriptor).asInstanceOf[_root_.com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber)
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.jalebi.proto.generated.jobmanagement.TaskRequest] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.jalebi.proto.generated.jobmanagement.TaskRequest(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        com.jalebi.proto.generated.jobmanagement.TaskOperation.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(com.jalebi.proto.generated.jobmanagement.TaskOperation.START.scalaValueDescriptor).number)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = JobmanagementProto.javaDescriptor.getMessageTypes.get(0)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = JobmanagementProto.scalaDescriptor.messages(0)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = {
    (__fieldNumber: @_root_.scala.unchecked) match {
      case 2 => com.jalebi.proto.generated.jobmanagement.TaskOperation
    }
  }
  lazy val defaultInstance = com.jalebi.proto.generated.jobmanagement.TaskRequest(
  )
  implicit class TaskRequestLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.generated.jobmanagement.TaskRequest]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.jalebi.proto.generated.jobmanagement.TaskRequest](_l) {
    def jobID: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.jobID)((c_, f_) => c_.copy(jobID = f_))
    def op: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.generated.jobmanagement.TaskOperation] = field(_.op)((c_, f_) => c_.copy(op = f_))
  }
  final val JOBID_FIELD_NUMBER = 1
  final val OP_FIELD_NUMBER = 2
}
