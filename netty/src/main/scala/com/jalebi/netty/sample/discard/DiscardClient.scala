package com.jalebi.netty.sample.discard

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

/**
  * Keeps sending random data to the specified address.
  */
object DiscardClient {
  val SSL = System.getProperty("ssl") != null
  val HOST = System.getProperty("host", "127.0.0.1")
  val PORT = System.getProperty("port", "8009").toInt
  val SIZE = System.getProperty("size", "256").toInt

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    val sslCtx  = if (SSL) SslContextBuilder.forClient.trustManager(InsecureTrustManagerFactory.INSTANCE).build else null
    val group = new NioEventLoopGroup
    try {
      val b = new Bootstrap
      b.group(group).channel(classOf[NioSocketChannel])
        .handler(new ChannelInitializer[SocketChannel]() {
          @throws[Exception]
        override protected def initChannel(ch: SocketChannel): Unit = {
          val p = ch.pipeline
          if (sslCtx != null) p.addLast(sslCtx.newHandler(ch.alloc, HOST, PORT))
          p.addLast(new DiscardClientHandler())
        }
      })
      // Make the connection attempt.
      val f = b.connect(HOST, PORT).sync
      // Wait until the connection is closed.
      f.channel.closeFuture.sync
    } finally group.shutdownGracefully()
  }
}