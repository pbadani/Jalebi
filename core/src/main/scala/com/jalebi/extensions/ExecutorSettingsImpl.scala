package com.jalebi.extensions

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.config.Config

class ExecutorSettingsImpl(config: Config) extends Extension {
  val protocol = config.getString("master.protocol")
  val masterSystem = config.getString("master.system")
  val masterActor = config.getString("master.actor")
  val monitorActor = config.getString("master.monitor")
}

object ExecutorSettings extends ExtensionId[ExecutorSettingsImpl] with ExtensionIdProvider {

  override def lookup = ExecutorSettings

  override def createExtension(system: ExtendedActorSystem) = new ExecutorSettingsImpl(system.settings.config)

  override def get(system: ActorSystem): ExecutorSettingsImpl = super.get(system)
}
