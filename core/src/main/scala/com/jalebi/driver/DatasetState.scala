package com.jalebi.driver

sealed trait DatasetState

object Noop extends DatasetState

object Loading extends DatasetState

object Loaded extends DatasetState