package com.ls.netty.heartbeat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author liushuang
 * @create 2019-12-07 17:24
 */
public class HeartbeatChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("Encoder", new StringEncoder());
        pipeline.addLast("Decoder", new StringDecoder());
        pipeline.addLast(new IdleStateHandler(4,7,10, TimeUnit.SECONDS));
        pipeline.addLast(new HeartBeatChannelHandler());
    }
}
