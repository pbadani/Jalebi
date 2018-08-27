package com.jalebi.common

object URIBuilder {

  def forLocalFile(resource: String) = s"file://$resource"
}
