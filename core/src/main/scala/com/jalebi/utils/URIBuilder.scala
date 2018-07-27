package com.jalebi.utils

object URIBuilder {

  def forLocalFile(resource: String) = s"file://$resource"
}
