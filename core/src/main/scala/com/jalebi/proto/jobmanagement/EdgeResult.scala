// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

@SerialVersionUID(0L)
final case class EdgeResult(
    sourceVertexId: _root_.scala.Long = 0L,
    targetVertexId: _root_.scala.Long = 0L,
    data: scala.collection.immutable.Map[_root_.scala.Predef.String, _root_.scala.Predef.String] = scala.collection.immutable.Map.empty
    ) extends scalapb.GeneratedMessage with scalapb.Message[EdgeResult] with scalapb.lenses.Updatable[EdgeResult] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (sourceVertexId != 0L) { __size += _root_.com.google.protobuf.CodedOutputStream.computeInt64Size(1, sourceVertexId) }
      if (targetVertexId != 0L) { __size += _root_.com.google.protobuf.CodedOutputStream.computeInt64Size(2, targetVertexId) }
      data.foreach(data => __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toBase(data).serializedSize) + com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toBase(data).serializedSize)
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
        val __v = sourceVertexId
        if (__v != 0L) {
          _output__.writeInt64(1, __v)
        }
      };
      {
        val __v = targetVertexId
        if (__v != 0L) {
          _output__.writeInt64(2, __v)
        }
      };
      data.foreach { __v =>
        _output__.writeTag(3, 2)
        _output__.writeUInt32NoTag(com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toBase(__v).serializedSize)
        com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toBase(__v).writeTo(_output__)
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.jalebi.proto.jobmanagement.EdgeResult = {
      var __sourceVertexId = this.sourceVertexId
      var __targetVertexId = this.targetVertexId
      val __data = (scala.collection.immutable.Map.newBuilder[_root_.scala.Predef.String, _root_.scala.Predef.String] ++= this.data)
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 8 =>
            __sourceVertexId = _input__.readInt64()
          case 16 =>
            __targetVertexId = _input__.readInt64()
          case 26 =>
            __data += com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toCustom(_root_.scalapb.LiteParser.readMessage(_input__, com.jalebi.proto.jobmanagement.EdgeResult.DataEntry.defaultInstance))
          case tag => _input__.skipField(tag)
        }
      }
      com.jalebi.proto.jobmanagement.EdgeResult(
          sourceVertexId = __sourceVertexId,
          targetVertexId = __targetVertexId,
          data = __data.result()
      )
    }
    def withSourceVertexId(__v: _root_.scala.Long): EdgeResult = copy(sourceVertexId = __v)
    def withTargetVertexId(__v: _root_.scala.Long): EdgeResult = copy(targetVertexId = __v)
    def clearData = copy(data = scala.collection.immutable.Map.empty)
    def addData(__vs: (_root_.scala.Predef.String, _root_.scala.Predef.String)*): EdgeResult = addAllData(__vs)
    def addAllData(__vs: TraversableOnce[(_root_.scala.Predef.String, _root_.scala.Predef.String)]): EdgeResult = copy(data = data ++ __vs)
    def withData(__v: scala.collection.immutable.Map[_root_.scala.Predef.String, _root_.scala.Predef.String]): EdgeResult = copy(data = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = sourceVertexId
          if (__t != 0L) __t else null
        }
        case 2 => {
          val __t = targetVertexId
          if (__t != 0L) __t else null
        }
        case 3 => data.map(com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toBase)(_root_.scala.collection.breakOut)
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PLong(sourceVertexId)
        case 2 => _root_.scalapb.descriptors.PLong(targetVertexId)
        case 3 => _root_.scalapb.descriptors.PRepeated(data.map(com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toBase(_).toPMessage)(_root_.scala.collection.breakOut))
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.jalebi.proto.jobmanagement.EdgeResult
}

object EdgeResult extends scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.EdgeResult] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.EdgeResult] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.jalebi.proto.jobmanagement.EdgeResult = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.jalebi.proto.jobmanagement.EdgeResult(
      __fieldsMap.getOrElse(__fields.get(0), 0L).asInstanceOf[_root_.scala.Long],
      __fieldsMap.getOrElse(__fields.get(1), 0L).asInstanceOf[_root_.scala.Long],
      __fieldsMap.getOrElse(__fields.get(2), Nil).asInstanceOf[_root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry]].map(com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toCustom)(_root_.scala.collection.breakOut)
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.jalebi.proto.jobmanagement.EdgeResult] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.jalebi.proto.jobmanagement.EdgeResult(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Long]).getOrElse(0L),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Long]).getOrElse(0L),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).map(_.as[_root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry]]).getOrElse(_root_.scala.collection.Seq.empty).map(com.jalebi.proto.jobmanagement.EdgeResult._typemapper_data.toCustom)(_root_.scala.collection.breakOut)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = JobmanagementProto.javaDescriptor.getMessageTypes.get(5)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = JobmanagementProto.scalaDescriptor.messages(5)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 3 => __out = com.jalebi.proto.jobmanagement.EdgeResult.DataEntry
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq[_root_.scalapb.GeneratedMessageCompanion[_]](
    _root_.com.jalebi.proto.jobmanagement.EdgeResult.DataEntry
  )
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.jalebi.proto.jobmanagement.EdgeResult(
  )
  @SerialVersionUID(0L)
  final case class DataEntry(
      key: _root_.scala.Predef.String = "",
      value: _root_.scala.Predef.String = ""
      ) extends scalapb.GeneratedMessage with scalapb.Message[DataEntry] with scalapb.lenses.Updatable[DataEntry] {
      @transient
      private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
      private[this] def __computeSerializedValue(): _root_.scala.Int = {
        var __size = 0
        if (key != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, key) }
        if (value != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, value) }
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
          val __v = key
          if (__v != "") {
            _output__.writeString(1, __v)
          }
        };
        {
          val __v = value
          if (__v != "") {
            _output__.writeString(2, __v)
          }
        };
      }
      def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.jalebi.proto.jobmanagement.EdgeResult.DataEntry = {
        var __key = this.key
        var __value = this.value
        var _done__ = false
        while (!_done__) {
          val _tag__ = _input__.readTag()
          _tag__ match {
            case 0 => _done__ = true
            case 10 =>
              __key = _input__.readString()
            case 18 =>
              __value = _input__.readString()
            case tag => _input__.skipField(tag)
          }
        }
        com.jalebi.proto.jobmanagement.EdgeResult.DataEntry(
            key = __key,
            value = __value
        )
      }
      def withKey(__v: _root_.scala.Predef.String): DataEntry = copy(key = __v)
      def withValue(__v: _root_.scala.Predef.String): DataEntry = copy(value = __v)
      def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
        (__fieldNumber: @_root_.scala.unchecked) match {
          case 1 => {
            val __t = key
            if (__t != "") __t else null
          }
          case 2 => {
            val __t = value
            if (__t != "") __t else null
          }
        }
      }
      def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
        require(__field.containingMessage eq companion.scalaDescriptor)
        (__field.number: @_root_.scala.unchecked) match {
          case 1 => _root_.scalapb.descriptors.PString(key)
          case 2 => _root_.scalapb.descriptors.PString(value)
        }
      }
      def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
      def companion = com.jalebi.proto.jobmanagement.EdgeResult.DataEntry
  }
  
  object DataEntry extends scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry] {
    implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry] = this
    def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.jalebi.proto.jobmanagement.EdgeResult.DataEntry = {
      require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
      val __fields = javaDescriptor.getFields
      com.jalebi.proto.jobmanagement.EdgeResult.DataEntry(
        __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
        __fieldsMap.getOrElse(__fields.get(1), "").asInstanceOf[_root_.scala.Predef.String]
      )
    }
    implicit def messageReads: _root_.scalapb.descriptors.Reads[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry] = _root_.scalapb.descriptors.Reads{
      case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
        require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
        com.jalebi.proto.jobmanagement.EdgeResult.DataEntry(
          __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
          __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse("")
        )
      case _ => throw new RuntimeException("Expected PMessage")
    }
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = com.jalebi.proto.jobmanagement.EdgeResult.javaDescriptor.getNestedTypes.get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = com.jalebi.proto.jobmanagement.EdgeResult.scalaDescriptor.nestedMessages(0)
    def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
    lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
    def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
    lazy val defaultInstance = com.jalebi.proto.jobmanagement.EdgeResult.DataEntry(
    )
    implicit class DataEntryLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.EdgeResult.DataEntry]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.jalebi.proto.jobmanagement.EdgeResult.DataEntry](_l) {
      def key: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.key)((c_, f_) => c_.copy(key = f_))
      def value: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.value)((c_, f_) => c_.copy(value = f_))
    }
    final val KEY_FIELD_NUMBER = 1
    final val VALUE_FIELD_NUMBER = 2
    implicit val keyValueMapper: _root_.scalapb.TypeMapper[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry, (_root_.scala.Predef.String, _root_.scala.Predef.String)] =
      _root_.scalapb.TypeMapper[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry, (_root_.scala.Predef.String, _root_.scala.Predef.String)](__m => (__m.key, __m.value))(__p => com.jalebi.proto.jobmanagement.EdgeResult.DataEntry(__p._1, __p._2))
  }
  
  implicit class EdgeResultLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.EdgeResult]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.jalebi.proto.jobmanagement.EdgeResult](_l) {
    def sourceVertexId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Long] = field(_.sourceVertexId)((c_, f_) => c_.copy(sourceVertexId = f_))
    def targetVertexId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Long] = field(_.targetVertexId)((c_, f_) => c_.copy(targetVertexId = f_))
    def data: _root_.scalapb.lenses.Lens[UpperPB, scala.collection.immutable.Map[_root_.scala.Predef.String, _root_.scala.Predef.String]] = field(_.data)((c_, f_) => c_.copy(data = f_))
  }
  final val SOURCEVERTEXID_FIELD_NUMBER = 1
  final val TARGETVERTEXID_FIELD_NUMBER = 2
  final val DATA_FIELD_NUMBER = 3
  @transient
  private val _typemapper_data: _root_.scalapb.TypeMapper[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry, (_root_.scala.Predef.String, _root_.scala.Predef.String)] = implicitly[_root_.scalapb.TypeMapper[com.jalebi.proto.jobmanagement.EdgeResult.DataEntry, (_root_.scala.Predef.String, _root_.scala.Predef.String)]]
}