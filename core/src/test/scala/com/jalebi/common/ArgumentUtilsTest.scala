package com.jalebi.common

import org.scalatest.{FlatSpec, Matchers}

class ArgumentUtilsTest extends FlatSpec with Matchers {

  "ArgumentUtils" should "just pass through when all the keys are present." in {
    val keys = Seq("key1", "key2", "key3")
    val options = Map("key1" -> "value1", "key2" -> "value2", "key3" -> "value3")
    val usage = "Some test usage."
    ArgumentUtils.validateArgPresent(keys, options, usage)
  }

  it should "throw an exception when the key is not present" in {
    val keys = Seq("key1", "key2", "key4")
    val options = Map("key1" -> "value1", "key2" -> "value2", "key3" -> "value3")
    val usage = "Some test usage."
    assertThrows[IllegalArgumentException] {
      ArgumentUtils.validateArgPresent(keys, options, usage)
    }
  }
}
