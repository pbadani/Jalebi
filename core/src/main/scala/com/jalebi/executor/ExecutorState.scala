package com.jalebi.executor

trait ExecutorState

object New extends ExecutorState

object Registered extends ExecutorState

object Loaded extends ExecutorState

object Unregistered extends ExecutorState

object RunningJob extends ExecutorState

object Runnable extends ExecutorState

object AssumedDead extends ExecutorState
