// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.proto.generated.stream

@SerialVersionUID(0L)
final case class StreamHelloResponse(
    message: _root_.scala.Predef.String = ""
    ) extends scalapb.GeneratedMessage with scalapb.Message[StreamHelloResponse] with scalapb.lenses.Updatable[StreamHelloResponse] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (message != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, message) }
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
        val __v = message
        if (__v != "") {
          _output__.writeString(1, __v)
        }
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.proto.generated.stream.StreamHelloResponse = {
      var __message = this.message
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __message = _input__.readString()
          case tag => _input__.skipField(tag)
        }
      }
      com.proto.generated.stream.StreamHelloResponse(
          message = __message
      )
    }
    def withMessage(__v: _root_.scala.Predef.String): StreamHelloResponse = copy(message = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = message
          if (__t != "") __t else null
        }
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(message)
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.proto.generated.stream.StreamHelloResponse
}

object StreamHelloResponse extends scalapb.GeneratedMessageCompanion[com.proto.generated.stream.StreamHelloResponse] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.proto.generated.stream.StreamHelloResponse] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.proto.generated.stream.StreamHelloResponse = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.proto.generated.stream.StreamHelloResponse(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String]
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.proto.generated.stream.StreamHelloResponse] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.proto.generated.stream.StreamHelloResponse(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse("")
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = StreamProto.javaDescriptor.getMessageTypes.get(1)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = StreamProto.scalaDescriptor.messages(1)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.proto.generated.stream.StreamHelloResponse(
  )
  implicit class StreamHelloResponseLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.proto.generated.stream.StreamHelloResponse]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.proto.generated.stream.StreamHelloResponse](_l) {
    def message: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.message)((c_, f_) => c_.copy(message = f_))
  }
  final val MESSAGE_FIELD_NUMBER = 1
}
