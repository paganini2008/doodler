package com.github.doodler.common.transmitter;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @Description: NettyClientKeepAlivePolicy
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
public class NettyClientKeepAlivePolicy extends KeepAlivePolicy {

    protected void whenWriterIdle(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(Packet.PING);
    }

}
