package com.jalebi.context

import java.net.URI

case class JalebiConfig private(appName: String, master: String)

object JalebiConfig {

  private case class JalebiConfigBuilder(appName: Option[String], master: Option[String]) {

    def withMaster(master: String): JalebiConfigBuilder = {
      val uri = new URI(master)
      require(uri.getScheme == "jalebi")
      require(uri.getHost.nonEmpty)
      this.copy(master = Some(master))
    }

    def fry(): JalebiConfig = {
      require(master.isDefined)
      require(appName.isDefined)
      JalebiConfig(appName.get, master.get)
    }
  }

  def withAppName(appName: String): JalebiConfigBuilder = {
    require(appName.nonEmpty)
    JalebiConfigBuilder(Some(appName), None)
  }

  def apply(appName: String, master: String): JalebiConfig = new JalebiConfig(appName, master)
}
