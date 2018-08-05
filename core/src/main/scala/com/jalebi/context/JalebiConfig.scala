package com.jalebi.context

import java.net.URI

import com.jalebi.hdfs.HostPort

case class JalebiConfig private(appName: String, master: String, hdfsHostPort: Option[HostPort])

object JalebiConfig {

  case class JalebiConfigBuilder(appName: Option[String], master: Option[String], hdfsHostPort: Option[HostPort]) {

    def withMaster(master: String): JalebiConfigBuilder = {
      master match {
        case "local" => this.copy(master = Some(master))
        case _ =>
          val uri = new URI(master)
          require(uri.getScheme == "jalebi")
          require(uri.getHost.nonEmpty)
          this.copy(master = Some(master))
      }
    }

    def withHDFSFileSystem(host: String, port: String): JalebiConfigBuilder = {
      require(host.nonEmpty)
      require(port.nonEmpty)
      this.copy(hdfsHostPort = Some(HostPort(host, port)))
    }

    def fry(): JalebiConfig = {
      require(master.isDefined)
      require(appName.isDefined)
      JalebiConfig(appName.get, master.get, hdfsHostPort)
    }
  }

  def withAppName(appName: String): JalebiConfigBuilder = {
    require(appName.nonEmpty)
    JalebiConfigBuilder(Some(appName), None, None)
  }

  def apply(appName: String, master: String, hostPort: Option[HostPort]): JalebiConfig = new JalebiConfig(appName, master, hostPort)
}
