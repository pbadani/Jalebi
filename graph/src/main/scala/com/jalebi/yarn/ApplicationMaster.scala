package com.jalebi.yarn

object ApplicationMaster {
  type ResponseCode = Integer

  def main(args: Array[String]): Unit = {
//    val amArgs = AMArgs(args)
    System.exit(run(null))
  }

  def run(args: AMArgs): ResponseCode = {
    println("Inside Application Master.")
    0
  }
}
