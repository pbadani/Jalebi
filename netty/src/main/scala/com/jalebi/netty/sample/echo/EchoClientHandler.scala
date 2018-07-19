package com.jalebi.netty.sample.echo

import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

class EchoClientHandler extends ChannelInboundHandlerAdapter {

  private val firstMessage: ByteBuf = Unpooled.buffer(EchoClient.SIZE)
  for (i <- 0 to firstMessage.capacity()) {
    firstMessage.writeByte(i.asInstanceOf[Byte])
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(firstMessage)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    println("==============")
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
