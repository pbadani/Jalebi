package com.jalebi.netty.sample.echo

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}


/**
  * Echoes back any received data from a client.
  */
object EchoServer {
  private[echo] val SSL = System.getProperty("ssl") != null
  private[echo] val PORT = System.getProperty("port", "8007").toInt

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    // Configure the server.
    val bossGroup = new NioEventLoopGroup(1)
    val workerGroup = new NioEventLoopGroup
    val serverHandler = new EchoServerHandler()
    try {
      val b = new ServerBootstrap
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer[SocketChannel]() {
          @throws[Exception]
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline.addLast(serverHandler)
          }
        })
      // Start the server.
      val f = b.bind(PORT).sync
      // Wait until the server socket is closed.
      f.channel.closeFuture.sync
    } finally {
      // Shut down all event loops to terminate all threads.
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }
}