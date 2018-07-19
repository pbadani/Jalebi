package com.jalebi.example

import com.jalebi.context.JalebiConfig

class Test {
  def main(args: Array[String]): Unit = {
    val jalebiConfig = JalebiConfig
      .withAppName("Jalebi")
      .withClusterMaster("jalebi://localhost:8080")
      .fry()

    val jalebiContext = JalebiConfig
  }
}
