package com.jalebi.hdfs

import com.jalebi.api._
import com.jalebi.exception.DatasetNotFoundException
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class HDFSClientTest extends FlatSpec with Matchers with BeforeAndAfter {

  private val hdfsClient = HDFSClient.withLocalFileSystem(new YarnConfiguration())

  before {
    hdfsClient.deleteDirectory()
  }

  after {
    hdfsClient.deleteDirectory()
  }

  val testTriplets = Seq(
    Triplets(Seq(
      Triplet(
        Node(Node(1), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(Node(1), Node(2), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Node(Node(2), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Node(Node(3), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(Node(3), Node(4), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Node(Node(4), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Node(Node(5), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(Node(5), Node(6), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Node(Node(6), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      )
    )),
    Triplets(Seq(
      Triplet(
        Node(Node(7), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(Node(7), Node(8), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Node(Node(8), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Node(Node(9), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(Node(9), Node(10), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Node(Node(10), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      ),
      Triplet(
        Node(Node(11), Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Edge(Node(11), Node(12), Map("TestKey5" -> "TestValue5", "TestKey6" -> "TestValue6"), isDirected = false),
        Node(Node(12), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      )
    ))
  )

  "HDFSClient" should "create the dataset on the given file system." in {
    val testDataset = "TestDataset1"
    hdfsClient.createDataset(testDataset, testTriplets.iterator)

    hdfsClient.datasetExists(testDataset) shouldBe true

    val parts = hdfsClient.listDatasetParts(testDataset)
    parts shouldBe Set("part-0", "part-1")

    hdfsClient.deleteDataset(testDataset)
    hdfsClient.datasetExists(testDataset) shouldBe false
  }

  it should "delete all the datasets in the directory." in {
    val testDataset = "TestDataset1"
    hdfsClient.createDataset(testDataset, testTriplets.iterator)

    val testDataset2 = "TestDataset2"
    hdfsClient.createDataset(testDataset2, testTriplets.iterator)

    hdfsClient.datasetExists(testDataset) shouldBe true
    hdfsClient.datasetExists(testDataset2) shouldBe true

    hdfsClient.deleteDirectory()

    hdfsClient.datasetExists(testDataset) shouldBe false
    hdfsClient.datasetExists(testDataset2) shouldBe false
  }

  it should "should throw DatasetNotFoundException if ensuring dataset exists for a missing one." in {
    hdfsClient.deleteDirectory()
    val testDataset = "TestDataset1"
    assertThrows[DatasetNotFoundException] {
      hdfsClient.ensureDatasetExists(testDataset)
    }
  }

  it should "list datasets found in the directory." in {
    val testDataset = "TestDataset1"
    hdfsClient.createDataset(testDataset, testTriplets.iterator)

    val testDataset2 = "TestDataset2"
    hdfsClient.createDataset(testDataset2, testTriplets.iterator)

    hdfsClient.listDatasets() shouldBe Set(testDataset, testDataset2)
  }
}
