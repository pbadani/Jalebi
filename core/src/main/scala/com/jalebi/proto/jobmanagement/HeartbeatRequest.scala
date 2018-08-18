// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

@SerialVersionUID(0L)
final case class HeartbeatRequest(
    executorId: _root_.scala.Predef.String = "",
    state: com.jalebi.proto.jobmanagement.ExecutorState = com.jalebi.proto.jobmanagement.ExecutorState.NEW
    ) extends scalapb.GeneratedMessage with scalapb.Message[HeartbeatRequest] with scalapb.lenses.Updatable[HeartbeatRequest] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (executorId != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, executorId) }
      if (state != com.jalebi.proto.jobmanagement.ExecutorState.NEW) { __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(2, state.value) }
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
        val __v = executorId
        if (__v != "") {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = state
        if (__v != com.jalebi.proto.jobmanagement.ExecutorState.NEW) {
          _output__.writeEnum(2, __v.value)
        }
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.jalebi.proto.jobmanagement.HeartbeatRequest = {
      var __executorId = this.executorId
      var __state = this.state
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __executorId = _input__.readString()
          case 16 =>
            __state = com.jalebi.proto.jobmanagement.ExecutorState.fromValue(_input__.readEnum())
          case tag => _input__.skipField(tag)
        }
      }
      com.jalebi.proto.jobmanagement.HeartbeatRequest(
          executorId = __executorId,
          state = __state
      )
    }
    def withExecutorId(__v: _root_.scala.Predef.String): HeartbeatRequest = copy(executorId = __v)
    def withState(__v: com.jalebi.proto.jobmanagement.ExecutorState): HeartbeatRequest = copy(state = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = executorId
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = state.javaValueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(executorId)
        case 2 => _root_.scalapb.descriptors.PEnum(state.scalaValueDescriptor)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.jalebi.proto.jobmanagement.HeartbeatRequest
}

object HeartbeatRequest extends scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.HeartbeatRequest] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.HeartbeatRequest] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.jalebi.proto.jobmanagement.HeartbeatRequest = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.jalebi.proto.jobmanagement.HeartbeatRequest(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
      com.jalebi.proto.jobmanagement.ExecutorState.fromValue(__fieldsMap.getOrElse(__fields.get(1), com.jalebi.proto.jobmanagement.ExecutorState.NEW.javaValueDescriptor).asInstanceOf[_root_.com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber)
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.jalebi.proto.jobmanagement.HeartbeatRequest] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.jalebi.proto.jobmanagement.HeartbeatRequest(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        com.jalebi.proto.jobmanagement.ExecutorState.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(com.jalebi.proto.jobmanagement.ExecutorState.NEW.scalaValueDescriptor).number)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = JobmanagementProto.javaDescriptor.getMessageTypes.get(2)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = JobmanagementProto.scalaDescriptor.messages(2)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = {
    (__fieldNumber: @_root_.scala.unchecked) match {
      case 2 => com.jalebi.proto.jobmanagement.ExecutorState
    }
  }
  lazy val defaultInstance = com.jalebi.proto.jobmanagement.HeartbeatRequest(
  )
  implicit class HeartbeatRequestLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.HeartbeatRequest]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.jalebi.proto.jobmanagement.HeartbeatRequest](_l) {
    def executorId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.executorId)((c_, f_) => c_.copy(executorId = f_))
    def state: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.ExecutorState] = field(_.state)((c_, f_) => c_.copy(state = f_))
  }
  final val EXECUTORID_FIELD_NUMBER = 1
  final val STATE_FIELD_NUMBER = 2
}