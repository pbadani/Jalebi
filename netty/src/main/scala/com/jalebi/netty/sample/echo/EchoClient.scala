package com.jalebi.netty.sample.echo

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory

object EchoClient {
  private[echo] val SSL = System.getProperty("ssl") != null
  private[echo] val HOST = System.getProperty("host", "127.0.0.1")
  private[echo] val PORT = System.getProperty("port", "8007").toInt
  private[echo] val SIZE = System.getProperty("size", "256").toInt

  @throws[Exception]
  def main(args: Array[String]): Unit = { // Configure SSL.git
    val sslCtx =  if (SSL)  SslContextBuilder.forClient.trustManager(InsecureTrustManagerFactory.INSTANCE).build
    else null
    // Configure the client.
    val group = new NioEventLoopGroup
    try {
      val b = new Bootstrap
      b.group(group)
        .channel(classOf[NioSocketChannel])
//        .option(ChannelOption.TCP_NODELAY, true.asInstanceOf[Any])
        .handler(new ChannelInitializer[SocketChannel]() {
        @throws[Exception]
        override def initChannel(ch: SocketChannel): Unit = {
          val p = ch.pipeline
          if (sslCtx != null) p.addLast(sslCtx.newHandler(ch.alloc, HOST, PORT))
          //p.addLast(new LoggingHandler(LogLevel.INFO));
          p.addLast(new EchoClientHandler())
        }
      })
      // Start the client.
      val f = b.connect(HOST, PORT).sync
      // Wait until the connection is closed.
      f.channel.closeFuture.sync
    } finally {
      // Shut down the event loop to terminate all threads.
      group.shutdownGracefully()
    }
  }
}

