package com.ls.proto.server.handler;
import com.ls.proto.MyDataInfo;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author heartccace
 * @create 2020-04-29 15:39
 * @Description TODO
 * @Version 1.0
 */
public class ProtocolBufChannelHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {
        System.out.println(msg);
    }
}