package com.jalebi.netty.sample.echo

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

/**
  * Handler implementation for the echo server.
  */
@Sharable class EchoServerHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    println("--------")
    println(msg.asInstanceOf[ByteBuf])
    ctx.write(msg)
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = { // Close the connection when an exception is raised.
    cause.printStackTrace()
    ctx.close
  }
}