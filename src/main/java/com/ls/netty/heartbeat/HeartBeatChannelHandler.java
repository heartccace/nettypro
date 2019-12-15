package com.ls.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author liushuang
 * @create 2019-12-07 17:28
 */
public class HeartBeatChannelHandler  extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            String eventState = "";
            switch (state) {
                case READER_IDLE:
                    eventState = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventState = "写空闲";
                    break;
                case ALL_IDLE:
                    eventState = "读写空闲";
                    break;
            }
            System.out.println("当前读写状态：" + eventState);
        }
    }
}
