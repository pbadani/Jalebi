package com.jalebi.context

import com.jalebi.api.{Edge, Node, Node}
import com.jalebi.hdfs.HDFSClient
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class JalebiContextTest extends FlatSpec with Matchers with BeforeAndAfter {

  private val hdfsClient = HDFSClient.withLocalFileSystem(new YarnConfiguration())

  before {
    hdfsClient.deleteDirectory()
  }

  after {
    hdfsClient.deleteDirectory()
  }

  private val conf = JalebiConfig
    .withAppName("TestApp")
    .withMaster("local")
    .withHDFSFileSystem("file", "localhost")
    .fry()

  "Context" should "create and load the dataset from local file system." in {
    val context = JalebiContext(conf)
    val jalebiWriter = new JalebiWriter {

      override def vertices[V]: Seq[Node] = Seq(
        Node(1, Map("TestKey1" -> "TestValue1", "TestKey2" -> "TestValue2")),
        Node(2, Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"))
      )

      override def datasetName: String = "TestDataset"

      override def edges[E]: Seq[Edge] = Seq(
        Edge(Node(1, Map.empty), Node(2, Map.empty), Map("TestKey3" -> "TestValue3", "TestKey4" -> "TestValue4"), isDirected = false)
      )
    }

    context.createDataset(jalebiWriter)
    hdfsClient.ensureDatasetExists("TestDataset")

    context.deleteDataset("TestDataset")
    hdfsClient.datasetExists("TestDataset") shouldBe false
  }

}
