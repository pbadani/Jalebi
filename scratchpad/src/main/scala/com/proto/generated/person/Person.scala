// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.proto.generated.person

@SerialVersionUID(0L)
final case class Person(
    name: _root_.scala.Predef.String = "",
    id: _root_.scala.Int = 0,
    email: _root_.scala.Predef.String = "",
    phone: _root_.scala.collection.Seq[com.proto.generated.person.Person.PhoneNumber] = _root_.scala.collection.Seq.empty
    ) extends scalapb.GeneratedMessage with scalapb.Message[Person] with scalapb.lenses.Updatable[Person] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (name != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, name) }
      if (id != 0) { __size += _root_.com.google.protobuf.CodedOutputStream.computeInt32Size(2, id) }
      if (email != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(3, email) }
      phone.foreach(phone => __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(phone.serializedSize) + phone.serializedSize)
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
        val __v = name
        if (__v != "") {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = id
        if (__v != 0) {
          _output__.writeInt32(2, __v)
        }
      };
      {
        val __v = email
        if (__v != "") {
          _output__.writeString(3, __v)
        }
      };
      phone.foreach { __v =>
        _output__.writeTag(4, 2)
        _output__.writeUInt32NoTag(__v.serializedSize)
        __v.writeTo(_output__)
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.proto.generated.person.Person = {
      var __name = this.name
      var __id = this.id
      var __email = this.email
      val __phone = (_root_.scala.collection.immutable.Vector.newBuilder[com.proto.generated.person.Person.PhoneNumber] ++= this.phone)
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __name = _input__.readString()
          case 16 =>
            __id = _input__.readInt32()
          case 26 =>
            __email = _input__.readString()
          case 34 =>
            __phone += _root_.scalapb.LiteParser.readMessage(_input__, com.proto.generated.person.Person.PhoneNumber.defaultInstance)
          case tag => _input__.skipField(tag)
        }
      }
      com.proto.generated.person.Person(
          name = __name,
          id = __id,
          email = __email,
          phone = __phone.result()
      )
    }
    def withName(__v: _root_.scala.Predef.String): Person = copy(name = __v)
    def withId(__v: _root_.scala.Int): Person = copy(id = __v)
    def withEmail(__v: _root_.scala.Predef.String): Person = copy(email = __v)
    def clearPhone = copy(phone = _root_.scala.collection.Seq.empty)
    def addPhone(__vs: com.proto.generated.person.Person.PhoneNumber*): Person = addAllPhone(__vs)
    def addAllPhone(__vs: TraversableOnce[com.proto.generated.person.Person.PhoneNumber]): Person = copy(phone = phone ++ __vs)
    def withPhone(__v: _root_.scala.collection.Seq[com.proto.generated.person.Person.PhoneNumber]): Person = copy(phone = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = name
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = id
          if (__t != 0) __t else null
        }
        case 3 => {
          val __t = email
          if (__t != "") __t else null
        }
        case 4 => phone
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(name)
        case 2 => _root_.scalapb.descriptors.PInt(id)
        case 3 => _root_.scalapb.descriptors.PString(email)
        case 4 => _root_.scalapb.descriptors.PRepeated(phone.map(_.toPMessage)(_root_.scala.collection.breakOut))
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.proto.generated.person.Person
}

object Person extends scalapb.GeneratedMessageCompanion[com.proto.generated.person.Person] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.proto.generated.person.Person] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.proto.generated.person.Person = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.proto.generated.person.Person(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
      __fieldsMap.getOrElse(__fields.get(1), 0).asInstanceOf[_root_.scala.Int],
      __fieldsMap.getOrElse(__fields.get(2), "").asInstanceOf[_root_.scala.Predef.String],
      __fieldsMap.getOrElse(__fields.get(3), Nil).asInstanceOf[_root_.scala.collection.Seq[com.proto.generated.person.Person.PhoneNumber]]
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.proto.generated.person.Person] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.proto.generated.person.Person(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Int]).getOrElse(0),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).map(_.as[_root_.scala.collection.Seq[com.proto.generated.person.Person.PhoneNumber]]).getOrElse(_root_.scala.collection.Seq.empty)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = PersonProto.javaDescriptor.getMessageTypes.get(0)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = PersonProto.scalaDescriptor.messages(0)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 4 => __out = com.proto.generated.person.Person.PhoneNumber
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq[_root_.scalapb.GeneratedMessageCompanion[_]](
    _root_.com.proto.generated.person.Person.PhoneNumber
  )
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = throw new MatchError(__fieldNumber)
  lazy val defaultInstance = com.proto.generated.person.Person(
  )
  sealed trait PhoneType extends _root_.scalapb.GeneratedEnum {
    type EnumType = PhoneType
    def isMobile: _root_.scala.Boolean = false
    def isHome: _root_.scala.Boolean = false
    def isWork: _root_.scala.Boolean = false
    def companion: _root_.scalapb.GeneratedEnumCompanion[PhoneType] = com.proto.generated.person.Person.PhoneType
  }
  
  object PhoneType extends _root_.scalapb.GeneratedEnumCompanion[PhoneType] {
    implicit def enumCompanion: _root_.scalapb.GeneratedEnumCompanion[PhoneType] = this
    @SerialVersionUID(0L)
    case object MOBILE extends PhoneType {
      val value = 0
      val index = 0
      val name = "MOBILE"
      override def isMobile: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object HOME extends PhoneType {
      val value = 1
      val index = 1
      val name = "HOME"
      override def isHome: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object WORK extends PhoneType {
      val value = 2
      val index = 2
      val name = "WORK"
      override def isWork: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    final case class Unrecognized(value: _root_.scala.Int) extends PhoneType with _root_.scalapb.UnrecognizedEnum
    
    lazy val values = scala.collection.Seq(MOBILE, HOME, WORK)
    def fromValue(value: _root_.scala.Int): PhoneType = value match {
      case 0 => MOBILE
      case 1 => HOME
      case 2 => WORK
      case __other => Unrecognized(__other)
    }
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.EnumDescriptor = com.proto.generated.person.Person.javaDescriptor.getEnumTypes.get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.EnumDescriptor = com.proto.generated.person.Person.scalaDescriptor.enums(0)
  }
  @SerialVersionUID(0L)
  final case class PhoneNumber(
      number: _root_.scala.Predef.String = "",
      `type`: com.proto.generated.person.Person.PhoneType = com.proto.generated.person.Person.PhoneType.MOBILE
      ) extends scalapb.GeneratedMessage with scalapb.Message[PhoneNumber] with scalapb.lenses.Updatable[PhoneNumber] {
      @transient
      private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
      private[this] def __computeSerializedValue(): _root_.scala.Int = {
        var __size = 0
        if (number != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, number) }
        if (`type` != com.proto.generated.person.Person.PhoneType.MOBILE) { __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(2, `type`.value) }
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
          val __v = number
          if (__v != "") {
            _output__.writeString(1, __v)
          }
        };
        {
          val __v = `type`
          if (__v != com.proto.generated.person.Person.PhoneType.MOBILE) {
            _output__.writeEnum(2, __v.value)
          }
        };
      }
      def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.proto.generated.person.Person.PhoneNumber = {
        var __number = this.number
        var __type = this.`type`
        var _done__ = false
        while (!_done__) {
          val _tag__ = _input__.readTag()
          _tag__ match {
            case 0 => _done__ = true
            case 10 =>
              __number = _input__.readString()
            case 16 =>
              __type = com.proto.generated.person.Person.PhoneType.fromValue(_input__.readEnum())
            case tag => _input__.skipField(tag)
          }
        }
        com.proto.generated.person.Person.PhoneNumber(
            number = __number,
            `type` = __type
        )
      }
      def withNumber(__v: _root_.scala.Predef.String): PhoneNumber = copy(number = __v)
      def withType(__v: com.proto.generated.person.Person.PhoneType): PhoneNumber = copy(`type` = __v)
      def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
        (__fieldNumber: @_root_.scala.unchecked) match {
          case 1 => {
            val __t = number
            if (__t != "") __t else null
          }
          case 2 => {
            val __t = `type`.javaValueDescriptor
            if (__t.getNumber() != 0) __t else null
          }
        }
      }
      def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
        require(__field.containingMessage eq companion.scalaDescriptor)
        (__field.number: @_root_.scala.unchecked) match {
          case 1 => _root_.scalapb.descriptors.PString(number)
          case 2 => _root_.scalapb.descriptors.PEnum(`type`.scalaValueDescriptor)
        }
      }
      def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
      def companion = com.proto.generated.person.Person.PhoneNumber
  }
  
  object PhoneNumber extends scalapb.GeneratedMessageCompanion[com.proto.generated.person.Person.PhoneNumber] {
    implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.proto.generated.person.Person.PhoneNumber] = this
    def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.proto.generated.person.Person.PhoneNumber = {
      require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
      val __fields = javaDescriptor.getFields
      com.proto.generated.person.Person.PhoneNumber(
        __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
        com.proto.generated.person.Person.PhoneType.fromValue(__fieldsMap.getOrElse(__fields.get(1), com.proto.generated.person.Person.PhoneType.MOBILE.javaValueDescriptor).asInstanceOf[_root_.com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber)
      )
    }
    implicit def messageReads: _root_.scalapb.descriptors.Reads[com.proto.generated.person.Person.PhoneNumber] = _root_.scalapb.descriptors.Reads{
      case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
        require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
        com.proto.generated.person.Person.PhoneNumber(
          __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
          com.proto.generated.person.Person.PhoneType.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(com.proto.generated.person.Person.PhoneType.MOBILE.scalaValueDescriptor).number)
        )
      case _ => throw new RuntimeException("Expected PMessage")
    }
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = com.proto.generated.person.Person.javaDescriptor.getNestedTypes.get(0)
    def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = com.proto.generated.person.Person.scalaDescriptor.nestedMessages(0)
    def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = throw new MatchError(__number)
    lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
    def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 2 => com.proto.generated.person.Person.PhoneType
      }
    }
    lazy val defaultInstance = com.proto.generated.person.Person.PhoneNumber(
    )
    implicit class PhoneNumberLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.proto.generated.person.Person.PhoneNumber]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.proto.generated.person.Person.PhoneNumber](_l) {
      def number: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.number)((c_, f_) => c_.copy(number = f_))
      def `type`: _root_.scalapb.lenses.Lens[UpperPB, com.proto.generated.person.Person.PhoneType] = field(_.`type`)((c_, f_) => c_.copy(`type` = f_))
    }
    final val NUMBER_FIELD_NUMBER = 1
    final val TYPE_FIELD_NUMBER = 2
  }
  
  implicit class PersonLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.proto.generated.person.Person]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.proto.generated.person.Person](_l) {
    def name: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.name)((c_, f_) => c_.copy(name = f_))
    def id: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Int] = field(_.id)((c_, f_) => c_.copy(id = f_))
    def email: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.email)((c_, f_) => c_.copy(email = f_))
    def phone: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.collection.Seq[com.proto.generated.person.Person.PhoneNumber]] = field(_.phone)((c_, f_) => c_.copy(phone = f_))
  }
  final val NAME_FIELD_NUMBER = 1
  final val ID_FIELD_NUMBER = 2
  final val EMAIL_FIELD_NUMBER = 3
  final val PHONE_FIELD_NUMBER = 4
}
