package com.jalebi.context

import java.net.URI

case class JalebiConfig private(appName: String, master: String)

object JalebiConfig {

  private class JalebiConfigBuilder {

    private var appName: String = _
    private var master: String = _

    def withClusterMaster(master: String): Unit = {
      val uri = new URI(master)
      require(uri.getScheme == "jalebi")
      require(uri.getHost.nonEmpty)
      this.master = master
    }

    def withAppName(appName: String): Unit = {
      require(appName.nonEmpty)
      this.appName = appName
    }

    def build(): JalebiConfig = {
      require(master.nonEmpty)
      require(appName.nonEmpty)
      JalebiConfig(appName, master)
    }
  }

  def createNew(): JalebiConfigBuilder = new JalebiConfigBuilder

  def apply(appName: String, master: String): JalebiConfig = new JalebiConfig(appName, master)
}
