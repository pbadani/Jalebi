// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

@SerialVersionUID(0L)
final case class HostPort(
    host: _root_.scala.Predef.String = "",
    port: _root_.scala.Predef.String = ""
    ) extends scalapb.GeneratedMessage with scalapb.Message[HostPort] with scalapb.lenses.Updatable[HostPort] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (host != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, host) }
      if (port != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, port) }
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
        val __v = host
        if (__v != "") {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = port
        if (__v != "") {
          _output__.writeString(2, __v)
        }
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.jalebi.proto.jobmanagement.HostPort = {
      var __host = this.host
      var __port = this.port
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __host = _input__.readString()
          case 18 =>
            __port = _input__.readString()
          case tag => _input__.skipField(tag)
        }
      }
      com.jalebi.proto.jobmanagement.HostPort(
          host = __host,
          port = __port
      )
    }
    def withHost(__v: _root_.scala.Predef.String): HostPort = copy(host = __v)
    def withPort(__v: _root_.scala.Predef.String): HostPort = copy(port = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = host
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = port
          if (__t != "") __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(host)
        case 2 => _root_.scalapb.descriptors.PString(port)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.jalebi.proto.jobmanagement.HostPort
}

object HostPort extends scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.HostPort] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.HostPort] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.jalebi.proto.jobmanagement.HostPort = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.jalebi.proto.jobmanagement.HostPort(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
      __fieldsMap.getOrElse(__fields.get(1), "").asInstanceOf[_root_.scala.Predef.String]
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.jalebi.proto.jobmanagement.HostPort] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.jalebi.proto.jobmanagement.HostPort(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse("")
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = JobmanagementProto.javaDescriptor.getMessageTypes.get(4)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = JobmanagementProto.scalaDescriptor.messages(4)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.jalebi.proto.jobmanagement.HostPort(
  )
  implicit class HostPortLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.HostPort]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.jalebi.proto.jobmanagement.HostPort](_l) {
    def host: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.host)((c_, f_) => c_.copy(host = f_))
    def port: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.port)((c_, f_) => c_.copy(port = f_))
  }
  final val HOST_FIELD_NUMBER = 1
  final val PORT_FIELD_NUMBER = 2
}
