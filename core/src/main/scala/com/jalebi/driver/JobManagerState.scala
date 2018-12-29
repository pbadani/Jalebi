package com.jalebi.driver

sealed trait JobManagerState

object UnInitialized extends JobManagerState

object Initializing extends JobManagerState

object Initialized extends JobManagerState

object DatasetLoaded extends JobManagerState
