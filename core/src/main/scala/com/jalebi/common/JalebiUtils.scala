package com.jalebi.common

import org.apache.hadoop.fs.RemoteIterator
import org.apache.hadoop.security.UserGroupInformation
import org.apache.hadoop.yarn.api.ApplicationConstants

import scala.collection.mutable

object JalebiUtils {

  /**
    * This is the path (suffix) at which the resources are loaded in HDFS for current application.
    */
  def getResourcePath(applicationId: String, resource: String): String = {
    require(applicationId.nonEmpty)
    require(resource.nonEmpty)
    s"jalebi/$applicationId/$resource"
  }

  def getJalebiHomePath(applicationId: String, resource: String): String = {
    require(applicationId.nonEmpty)
    require(resource.nonEmpty)
    s"jalebi/$applicationId/jalebihome/$resource"
  }

  def addPathToEnvironment(env: mutable.HashMap[String, String], key: String, value: String): Unit = {
    val newValue = if (env.contains(key)) {
      env(key) + ApplicationConstants.CLASS_PATH_SEPARATOR + value
    } else {
      value
    }
    env.put(key, newValue)
  }

  def createUser(): UserGroupInformation = {
    val currentUser = UserGroupInformation.getCurrentUser
    val username = currentUser.getShortUserName
    val ugi = UserGroupInformation.createRemoteUser(username)
    ugi.addCredentials(currentUser.getCredentials)
    ugi
  }

  implicit class RemoteFileIterator[T](i: RemoteIterator[T]) extends Iterator[T] {
    override def hasNext: Boolean = i.hasNext

    override def next(): T = i.next()
  }

}
