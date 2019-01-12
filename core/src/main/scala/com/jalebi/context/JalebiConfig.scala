package com.jalebi.context

import java.net.URI

import com.jalebi.hdfs.HostPort

case class JalebiConfig private(appName: String, master: String, hdfsHostPort: Option[HostPort], options: JalebiConfigOptions)

object JalebiConfig {

  implicit def confToOptions(conf: JalebiConfig): JalebiConfigOptions = conf.options

  case class JalebiConfigBuilder(appName: Option[String], master: Option[String], hdfsHostPort: Option[HostPort], options: Map[String, String]) {

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

    def withHDFSFileSystem(scheme: String, host: String, port: Long = 0): JalebiConfigBuilder = {
      require(scheme.nonEmpty)
      require(host.nonEmpty)
      this.copy(hdfsHostPort = Some(new HostPort(scheme, host, port)))
    }

    def withOptions(options: Map[String, String]): JalebiConfigBuilder = {
      this.copy(options = options.foldLeft(Map.empty[String, String])((m, t) => m + t))
    }

    def fry(): JalebiConfig = {
      require(master.isDefined)
      require(appName.isDefined)
      JalebiConfig(appName.get, master.get, hdfsHostPort, JalebiConfigOptions(options))
    }
  }

  def withAppName(appName: String): JalebiConfigBuilder = {
    require(appName.nonEmpty)
    JalebiConfigBuilder(Some(appName), None, None, Map.empty)
  }

  def apply(appName: String, master: String, hostPort: Option[HostPort], options: JalebiConfigOptions): JalebiConfig = new JalebiConfig(appName, master, hostPort, options)
}
