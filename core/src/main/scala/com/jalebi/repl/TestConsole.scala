package com.jalebi.repl

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop

object TestConsole extends App {
  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true

  new SampleILoop().process(settings)
}

class SampleILoop extends ILoop {
  override def prompt = "==> "

  //  addThunk {
  //    intp.beQuietDuring {
  //      intp.addImports("java.lang.Math._")
  //    }
  //  }

  override def printWelcome() {
    echo("\n" +
      "         \\,,,/\n" +
      "         (o o)\n" +
      "-----oOOo-(_)-oOOo-----")
  }

}