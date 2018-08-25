package com.jalebi.hdfs

import com.jalebi.api._
import com.jalebi.exception.DatasetNotFoundException
import org.scalatest.{FlatSpec, Matchers}

class HDFSClientTest extends FlatSpec with Matchers {

  val hdfsClient = HDFSClient.withLocalFileSystem()
  hdfsClient.deleteDirectory()

  val testTriplets = Seq(
    Triplets(Seq(
      Triplet(
        Vertex(VertexID(1), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(VertexID(1), VertexID(2), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Vertex(VertexID(2), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Vertex(VertexID(3), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(VertexID(3), VertexID(4), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Vertex(VertexID(4), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Vertex(VertexID(5), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(VertexID(5), VertexID(6), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Vertex(VertexID(6), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      )
    )),
    Triplets(Seq(
      Triplet(
        Vertex(VertexID(7), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(VertexID(7), VertexID(8), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Vertex(VertexID(8), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Vertex(VertexID(9), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(VertexID(9), VertexID(10), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Vertex(VertexID(10), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Vertex(VertexID(11), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(VertexID(11), VertexID(12), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Vertex(VertexID(12), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      )
    ))
  )

  "HDFSClient" should "create the dataset on the given file system." in {
    val testDataset = "TestDataset1"
    hdfsClient.createDataset(testDataset, testTriplets.iterator)

    hdfsClient.doesDatasetExists(testDataset) shouldBe true

    val parts = hdfsClient.listDatasetParts(testDataset)
    parts shouldBe Set("part-0", "part-1")

    hdfsClient.deleteDataset(testDataset)
    hdfsClient.doesDatasetExists(testDataset) shouldBe false

    hdfsClient.deleteDirectory()
  }

  it should "delete all the datasets in the directory." in {
    val testDataset = "TestDataset1"
    hdfsClient.createDataset(testDataset, testTriplets.iterator)

    val testDataset2 = "TestDataset2"
    hdfsClient.createDataset(testDataset2, testTriplets.iterator)

    hdfsClient.doesDatasetExists(testDataset) shouldBe true
    hdfsClient.doesDatasetExists(testDataset2) shouldBe true

    hdfsClient.deleteDirectory()

    hdfsClient.doesDatasetExists(testDataset) shouldBe false
    hdfsClient.doesDatasetExists(testDataset2) shouldBe false
  }

  it should "should throw DatasetNotFoundException if ensuring dataset exists for a missing one." in {
    hdfsClient.deleteDirectory()
    val testDataset = "TestDataset1"
    assertThrows[DatasetNotFoundException] {
      hdfsClient.ensureDatasetExists(testDataset)
    }
  }

  it should "list datasets found in the directory." in {
    hdfsClient.deleteDirectory()
    val testDataset = "TestDataset1"
    hdfsClient.createDataset(testDataset, testTriplets.iterator)

    val testDataset2 = "TestDataset2"
    hdfsClient.createDataset(testDataset2, testTriplets.iterator)

    hdfsClient.listDatasets() shouldBe Set(testDataset, testDataset2)
  }
}
