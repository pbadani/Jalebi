// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package com.jalebi.proto.jobmanagement

@SerialVersionUID(0L)
final case class TaskResponse(
    jobId: _root_.scala.Predef.String = "",
    executorId: _root_.scala.Predef.String = "",
    executorState: com.jalebi.proto.jobmanagement.ExecutorState = com.jalebi.proto.jobmanagement.ExecutorState.NEW,
    datasetState: com.jalebi.proto.jobmanagement.DatasetState = com.jalebi.proto.jobmanagement.DatasetState.NONE,
    vertexResults: _root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Vertex] = _root_.scala.collection.Seq.empty,
    edgeResults: _root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Edge] = _root_.scala.collection.Seq.empty
    ) extends scalapb.GeneratedMessage with scalapb.Message[TaskResponse] with scalapb.lenses.Updatable[TaskResponse] {
    @transient
    private[this] var __serializedSizeCachedValue: _root_.scala.Int = 0
    private[this] def __computeSerializedValue(): _root_.scala.Int = {
      var __size = 0
      if (jobId != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(1, jobId) }
      if (executorId != "") { __size += _root_.com.google.protobuf.CodedOutputStream.computeStringSize(2, executorId) }
      if (executorState != com.jalebi.proto.jobmanagement.ExecutorState.NEW) { __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(3, executorState.value) }
      if (datasetState != com.jalebi.proto.jobmanagement.DatasetState.NONE) { __size += _root_.com.google.protobuf.CodedOutputStream.computeEnumSize(4, datasetState.value) }
      vertexResults.foreach(vertexResults => __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(vertexResults.serializedSize) + vertexResults.serializedSize)
      edgeResults.foreach(edgeResults => __size += 1 + _root_.com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(edgeResults.serializedSize) + edgeResults.serializedSize)
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
        val __v = jobId
        if (__v != "") {
          _output__.writeString(1, __v)
        }
      };
      {
        val __v = executorId
        if (__v != "") {
          _output__.writeString(2, __v)
        }
      };
      {
        val __v = executorState
        if (__v != com.jalebi.proto.jobmanagement.ExecutorState.NEW) {
          _output__.writeEnum(3, __v.value)
        }
      };
      {
        val __v = datasetState
        if (__v != com.jalebi.proto.jobmanagement.DatasetState.NONE) {
          _output__.writeEnum(4, __v.value)
        }
      };
      vertexResults.foreach { __v =>
        _output__.writeTag(5, 2)
        _output__.writeUInt32NoTag(__v.serializedSize)
        __v.writeTo(_output__)
      };
      edgeResults.foreach { __v =>
        _output__.writeTag(6, 2)
        _output__.writeUInt32NoTag(__v.serializedSize)
        __v.writeTo(_output__)
      };
    }
    def mergeFrom(`_input__`: _root_.com.google.protobuf.CodedInputStream): com.jalebi.proto.jobmanagement.TaskResponse = {
      var __jobId = this.jobId
      var __executorId = this.executorId
      var __executorState = this.executorState
      var __datasetState = this.datasetState
      val __vertexResults = (_root_.scala.collection.immutable.Vector.newBuilder[com.jalebi.proto.jobmanagement.Vertex] ++= this.vertexResults)
      val __edgeResults = (_root_.scala.collection.immutable.Vector.newBuilder[com.jalebi.proto.jobmanagement.Edge] ++= this.edgeResults)
      var _done__ = false
      while (!_done__) {
        val _tag__ = _input__.readTag()
        _tag__ match {
          case 0 => _done__ = true
          case 10 =>
            __jobId = _input__.readString()
          case 18 =>
            __executorId = _input__.readString()
          case 24 =>
            __executorState = com.jalebi.proto.jobmanagement.ExecutorState.fromValue(_input__.readEnum())
          case 32 =>
            __datasetState = com.jalebi.proto.jobmanagement.DatasetState.fromValue(_input__.readEnum())
          case 42 =>
            __vertexResults += _root_.scalapb.LiteParser.readMessage(_input__, com.jalebi.proto.jobmanagement.Vertex.defaultInstance)
          case 50 =>
            __edgeResults += _root_.scalapb.LiteParser.readMessage(_input__, com.jalebi.proto.jobmanagement.Edge.defaultInstance)
          case tag => _input__.skipField(tag)
        }
      }
      com.jalebi.proto.jobmanagement.TaskResponse(
          jobId = __jobId,
          executorId = __executorId,
          executorState = __executorState,
          datasetState = __datasetState,
          vertexResults = __vertexResults.result(),
          edgeResults = __edgeResults.result()
      )
    }
    def withJobId(__v: _root_.scala.Predef.String): TaskResponse = copy(jobId = __v)
    def withExecutorId(__v: _root_.scala.Predef.String): TaskResponse = copy(executorId = __v)
    def withExecutorState(__v: com.jalebi.proto.jobmanagement.ExecutorState): TaskResponse = copy(executorState = __v)
    def withDatasetState(__v: com.jalebi.proto.jobmanagement.DatasetState): TaskResponse = copy(datasetState = __v)
    def clearVertexResults = copy(vertexResults = _root_.scala.collection.Seq.empty)
    def addVertexResults(__vs: com.jalebi.proto.jobmanagement.Vertex*): TaskResponse = addAllVertexResults(__vs)
    def addAllVertexResults(__vs: TraversableOnce[com.jalebi.proto.jobmanagement.Vertex]): TaskResponse = copy(vertexResults = vertexResults ++ __vs)
    def withVertexResults(__v: _root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Vertex]): TaskResponse = copy(vertexResults = __v)
    def clearEdgeResults = copy(edgeResults = _root_.scala.collection.Seq.empty)
    def addEdgeResults(__vs: com.jalebi.proto.jobmanagement.Edge*): TaskResponse = addAllEdgeResults(__vs)
    def addAllEdgeResults(__vs: TraversableOnce[com.jalebi.proto.jobmanagement.Edge]): TaskResponse = copy(edgeResults = edgeResults ++ __vs)
    def withEdgeResults(__v: _root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Edge]): TaskResponse = copy(edgeResults = __v)
    def getFieldByNumber(__fieldNumber: _root_.scala.Int): scala.Any = {
      (__fieldNumber: @_root_.scala.unchecked) match {
        case 1 => {
          val __t = jobId
          if (__t != "") __t else null
        }
        case 2 => {
          val __t = executorId
          if (__t != "") __t else null
        }
        case 3 => {
          val __t = executorState.javaValueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
        case 4 => {
          val __t = datasetState.javaValueDescriptor
          if (__t.getNumber() != 0) __t else null
        }
        case 5 => vertexResults
        case 6 => edgeResults
      }
    }
    def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue = {
      require(__field.containingMessage eq companion.scalaDescriptor)
      (__field.number: @_root_.scala.unchecked) match {
        case 1 => _root_.scalapb.descriptors.PString(jobId)
        case 2 => _root_.scalapb.descriptors.PString(executorId)
        case 3 => _root_.scalapb.descriptors.PEnum(executorState.scalaValueDescriptor)
        case 4 => _root_.scalapb.descriptors.PEnum(datasetState.scalaValueDescriptor)
        case 5 => _root_.scalapb.descriptors.PRepeated(vertexResults.map(_.toPMessage)(_root_.scala.collection.breakOut))
        case 6 => _root_.scalapb.descriptors.PRepeated(edgeResults.map(_.toPMessage)(_root_.scala.collection.breakOut))
      }
    }
    def toProtoString: _root_.scala.Predef.String = _root_.scalapb.TextFormat.printToUnicodeString(this)
    def companion = com.jalebi.proto.jobmanagement.TaskResponse
}

object TaskResponse extends scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.TaskResponse] {
  implicit def messageCompanion: scalapb.GeneratedMessageCompanion[com.jalebi.proto.jobmanagement.TaskResponse] = this
  def fromFieldsMap(__fieldsMap: scala.collection.immutable.Map[_root_.com.google.protobuf.Descriptors.FieldDescriptor, scala.Any]): com.jalebi.proto.jobmanagement.TaskResponse = {
    require(__fieldsMap.keys.forall(_.getContainingType() == javaDescriptor), "FieldDescriptor does not match message type.")
    val __fields = javaDescriptor.getFields
    com.jalebi.proto.jobmanagement.TaskResponse(
      __fieldsMap.getOrElse(__fields.get(0), "").asInstanceOf[_root_.scala.Predef.String],
      __fieldsMap.getOrElse(__fields.get(1), "").asInstanceOf[_root_.scala.Predef.String],
      com.jalebi.proto.jobmanagement.ExecutorState.fromValue(__fieldsMap.getOrElse(__fields.get(2), com.jalebi.proto.jobmanagement.ExecutorState.NEW.javaValueDescriptor).asInstanceOf[_root_.com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber),
      com.jalebi.proto.jobmanagement.DatasetState.fromValue(__fieldsMap.getOrElse(__fields.get(3), com.jalebi.proto.jobmanagement.DatasetState.NONE.javaValueDescriptor).asInstanceOf[_root_.com.google.protobuf.Descriptors.EnumValueDescriptor].getNumber),
      __fieldsMap.getOrElse(__fields.get(4), Nil).asInstanceOf[_root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Vertex]],
      __fieldsMap.getOrElse(__fields.get(5), Nil).asInstanceOf[_root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Edge]]
    )
  }
  implicit def messageReads: _root_.scalapb.descriptors.Reads[com.jalebi.proto.jobmanagement.TaskResponse] = _root_.scalapb.descriptors.Reads{
    case _root_.scalapb.descriptors.PMessage(__fieldsMap) =>
      require(__fieldsMap.keys.forall(_.containingMessage == scalaDescriptor), "FieldDescriptor does not match message type.")
      com.jalebi.proto.jobmanagement.TaskResponse(
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(1).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(2).get).map(_.as[_root_.scala.Predef.String]).getOrElse(""),
        com.jalebi.proto.jobmanagement.ExecutorState.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(3).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(com.jalebi.proto.jobmanagement.ExecutorState.NEW.scalaValueDescriptor).number),
        com.jalebi.proto.jobmanagement.DatasetState.fromValue(__fieldsMap.get(scalaDescriptor.findFieldByNumber(4).get).map(_.as[_root_.scalapb.descriptors.EnumValueDescriptor]).getOrElse(com.jalebi.proto.jobmanagement.DatasetState.NONE.scalaValueDescriptor).number),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(5).get).map(_.as[_root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Vertex]]).getOrElse(_root_.scala.collection.Seq.empty),
        __fieldsMap.get(scalaDescriptor.findFieldByNumber(6).get).map(_.as[_root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Edge]]).getOrElse(_root_.scala.collection.Seq.empty)
      )
    case _ => throw new RuntimeException("Expected PMessage")
  }
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.Descriptor = JobmanagementProto.javaDescriptor.getMessageTypes.get(3)
  def scalaDescriptor: _root_.scalapb.descriptors.Descriptor = JobmanagementProto.scalaDescriptor.messages(3)
  def messageCompanionForFieldNumber(__number: _root_.scala.Int): _root_.scalapb.GeneratedMessageCompanion[_] = {
    var __out: _root_.scalapb.GeneratedMessageCompanion[_] = null
    (__number: @_root_.scala.unchecked) match {
      case 5 => __out = com.jalebi.proto.jobmanagement.Vertex
      case 6 => __out = com.jalebi.proto.jobmanagement.Edge
    }
    __out
  }
  lazy val nestedMessagesCompanions: Seq[_root_.scalapb.GeneratedMessageCompanion[_]] = Seq.empty
  def enumCompanionForFieldNumber(__fieldNumber: _root_.scala.Int): _root_.scalapb.GeneratedEnumCompanion[_] = {
    (__fieldNumber: @_root_.scala.unchecked) match {
      case 3 => com.jalebi.proto.jobmanagement.ExecutorState
      case 4 => com.jalebi.proto.jobmanagement.DatasetState
    }
  }
  lazy val defaultInstance = com.jalebi.proto.jobmanagement.TaskResponse(
  )
  implicit class TaskResponseLens[UpperPB](_l: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.TaskResponse]) extends _root_.scalapb.lenses.ObjectLens[UpperPB, com.jalebi.proto.jobmanagement.TaskResponse](_l) {
    def jobId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.jobId)((c_, f_) => c_.copy(jobId = f_))
    def executorId: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.Predef.String] = field(_.executorId)((c_, f_) => c_.copy(executorId = f_))
    def executorState: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.ExecutorState] = field(_.executorState)((c_, f_) => c_.copy(executorState = f_))
    def datasetState: _root_.scalapb.lenses.Lens[UpperPB, com.jalebi.proto.jobmanagement.DatasetState] = field(_.datasetState)((c_, f_) => c_.copy(datasetState = f_))
    def vertexResults: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Vertex]] = field(_.vertexResults)((c_, f_) => c_.copy(vertexResults = f_))
    def edgeResults: _root_.scalapb.lenses.Lens[UpperPB, _root_.scala.collection.Seq[com.jalebi.proto.jobmanagement.Edge]] = field(_.edgeResults)((c_, f_) => c_.copy(edgeResults = f_))
  }
  final val JOBID_FIELD_NUMBER = 1
  final val EXECUTORID_FIELD_NUMBER = 2
  final val EXECUTORSTATE_FIELD_NUMBER = 3
  final val DATASETSTATE_FIELD_NUMBER = 4
  final val VERTEXRESULTS_FIELD_NUMBER = 5
  final val EDGERESULTS_FIELD_NUMBER = 6
}
