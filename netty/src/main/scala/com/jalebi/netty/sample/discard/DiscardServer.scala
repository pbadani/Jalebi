package com.jalebi.netty.sample.discard

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

object DiscardServer {

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    val port = if (args.length > 0) args(0).toInt else 8009
    DiscardServer(port).run()
  }
}

case class DiscardServer(port: Int) {

  @throws[Exception]
  def run(): Unit = {
    val bossGroup = new NioEventLoopGroup
    val workerGroup = new NioEventLoopGroup
    try {
      val b = new ServerBootstrap
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(
          new ChannelInitializer[SocketChannel]() {
            @throws[Exception]
            override def initChannel(ch: SocketChannel): Unit = {
              ch.pipeline().addLast(new DiscardServerHandler)
            }
          }
        )
//        .option(ChannelOption.SO_BACKLOG, 128)
//        .childOption(ChannelOption.SO_KEEPALIVE, true)
      val f = b.bind(port).sync
      f.channel.closeFuture.sync
    } finally {
      workerGroup.shutdownGracefully()
      bossGroup.shutdownGracefully()
    }
  }
}