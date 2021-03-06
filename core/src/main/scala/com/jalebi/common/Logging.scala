package com.jalebi.common

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  self: Any =>
  val LOGGER: Logger = LoggerFactory.getLogger(self.getClass.getName)
}
