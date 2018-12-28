package com.jalebi.message

trait Job

case class LoadDataset(name: String) extends Job
