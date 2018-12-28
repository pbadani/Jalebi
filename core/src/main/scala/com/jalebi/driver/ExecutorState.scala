package com.jalebi.driver

sealed trait ExecutorState

object NEW extends ExecutorState

object REQUESTED extends ExecutorState

object ALLOCATED extends ExecutorState

object REGISTERED extends ExecutorState

object LOADED extends ExecutorState

object RUNNING_JOB extends ExecutorState

object RUNNABLE extends ExecutorState

object UNREGISTERED extends ExecutorState

object ASSUMED_DEAD extends ExecutorState
