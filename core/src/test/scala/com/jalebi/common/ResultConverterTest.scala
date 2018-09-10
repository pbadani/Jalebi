package com.jalebi.common

import com.jalebi.api.{Vertex, VertexID}
import com.jalebi.proto.jobmanagement.VertexResult
import org.scalatest.{FlatSpec, Matchers}

class ResultConverterTest extends FlatSpec with Matchers {

  "ResultConverter" should "convert from vertex objects to the result." in {
    val vertexId = VertexID(1)
    val data = Map("key1" -> "value1", "key2" -> "value2")
    val expected = Seq(VertexResult(1, Map("key1" -> "value1", "key2" -> "value2")))
    val actual = ResultConverter.convertToVertexResult(Some(Vertex(vertexId, data)))
    expected shouldBe actual
  }

  it should "convert None to an empty sequence." in {
    val expected = Nil
    val actual = ResultConverter.convertToVertexResult(None)
    expected shouldBe actual
  }

  it should "convert a sequence of vertex objects to the expected result." in {
    val expected = Seq(
      VertexResult(1, Map("key1" -> "value1", "key2" -> "value2")),
      VertexResult(2, Map("key3" -> "value3", "key4" -> "value4"))
    )
    val vertexId1 = VertexID(1)
    val data1 = Map("key1" -> "value1", "key2" -> "value2")
    val vertexId2 = VertexID(2)
    val data2 = Map("key3" -> "value3", "key4" -> "value4")
    val vertices = Seq(
      Vertex(vertexId1, data1),
      Vertex(vertexId2, data2)
    )
    val actual = ResultConverter.convertToVertices(vertices)
    expected shouldBe actual
  }

  it should "handle an empty sequence of vertices." in {
    val expected = Nil
    val vertices = Nil
    val actual = ResultConverter.convertToVertices(vertices)
    expected shouldBe actual
  }

  it should "convert from vertex result objects back to the vertices in the api." in {
    val expected = Seq(
      Vertex(VertexID(1), Map("key1" -> "value1", "key2" -> "value2")),
      Vertex(VertexID(2), Map("key3" -> "value3", "key4" -> "value4"))
    )
    val data1 = Map("key1" -> "value1", "key2" -> "value2")
    val data2 = Map("key3" -> "value3", "key4" -> "value4")
    val vertexResults = Seq(
      VertexResult(1, data1),
      VertexResult(2, data2)
    )
    val actual = ResultConverter.convertFromVertices(vertexResults)
    expected shouldBe actual
  }

  it should "handle an empty sequence of vertex results." in {
    val expected = Nil
    val vertices = Nil
    val actual = ResultConverter.convertFromVertices(vertices)
    expected shouldBe actual
  }

  it should "should convert None vertex result object to an empty sequence" in {
    val expected = Nil
    val actual = ResultConverter.convertFromVertexResult(None)
    expected shouldBe actual
  }

}
