package com.ls.netty.tcp;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author liushuang
 * @create 2019-12-14 14:49
 */
public class MyServerChannelHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        System.out.println("---------------------收到客户端消息---------------------");
        byte[] data = msg.getData();
        System.out.println(new String(data));
    }
}
