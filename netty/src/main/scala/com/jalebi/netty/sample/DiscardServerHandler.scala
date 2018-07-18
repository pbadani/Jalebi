package com.jalebi.netty.sample

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class DiscardServerHandler extends ChannelInboundHandlerAdapter {

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = { // (2)
    // Discard the received data silently.
    msg.asInstanceOf[ByteBuf].release // (3)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = { // (4)
    // Close the connection when an exception is raised.
    cause.printStackTrace()
    ctx.close
  }
}
