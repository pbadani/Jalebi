package com.jalebi.extensions

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.jalebi.hdfs.HostPort
import com.typesafe.config.Config

class MasterSettingsImpl(config: Config) extends Extension {
  val host = config.getString("akka.remote.netty.tcp.hostname")
  val port = config.getLong("akka.remote.netty.tcp.port")

  def hostPort = HostPort("", host, port)
}

object MasterSettings extends ExtensionId[MasterSettingsImpl] with ExtensionIdProvider {

  override def lookup = MasterSettings

  override def createExtension(system: ExtendedActorSystem) = new MasterSettingsImpl(system.settings.config)

  override def get(system: ActorSystem): MasterSettingsImpl = super.get(system)
}
