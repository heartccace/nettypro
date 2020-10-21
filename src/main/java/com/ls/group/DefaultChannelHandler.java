package com.ls.group;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * @author heartccace
 * @create 2020-07-21 22:53
 * @Description TODO
 * @Version 1.0
 */
public class DefaultChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String s = msg.content().toString();
        System.out.println("客户端接收到消息：" + s);
    }
}
