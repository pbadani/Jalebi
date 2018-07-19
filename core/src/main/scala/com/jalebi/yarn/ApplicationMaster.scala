package com.jalebi.yarn

object ApplicationMaster {

  def main(args: Array[String]): Unit = {
    println("Test")
    //    val amArgs = AMArgs(args)
    System.exit(run(null))
  }

  def run(args: AMArgs): Integer = {
    println("Inside Application Master.")
    0
  }
}
