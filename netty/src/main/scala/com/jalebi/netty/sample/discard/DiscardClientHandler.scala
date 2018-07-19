package com.jalebi.netty.sample.discard

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}

/**
  * Handles a client-side channel.
  */
class DiscardClientHandler extends SimpleChannelInboundHandler[Any] {
  private var content: ByteBuf = null
  private var ctx: ChannelHandlerContext = null

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    this.ctx = ctx
    // Initialize the message.
    content = ctx.alloc.directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE)
    // Send the initial messages.
    generateTraffic()
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    content.release
  }

  @throws[Exception]
  override def channelRead0(ctx: ChannelHandlerContext, msg: Any): Unit = {
    // Server is supposed to send nothing, but if it sends something, discard it.
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = { // Close the connection when an exception is raised.
    cause.printStackTrace()
    ctx.close
  }

  private val counter = 0L

  private def generateTraffic(): Unit = { // Flush the outbound buffer to the socket.
    // Once flushed, generate the same amount of traffic again.
    ctx.writeAndFlush(content.retainedDuplicate).addListener(trafficGenerator)
  }

  final private val trafficGenerator = new ChannelFutureListener() {
    override def operationComplete(future: ChannelFuture): Unit = {
      if (future.isSuccess) generateTraffic()
      else {
        future.cause.printStackTrace()
        future.channel.close
      }
    }
  }
}