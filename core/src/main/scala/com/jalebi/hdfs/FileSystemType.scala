package com.jalebi.hdfs

object FileSystemType extends Enumeration {
  type FS = Value
  val HDFS_LOCAL, HDFS_DISTRIBUTED = Value
}
