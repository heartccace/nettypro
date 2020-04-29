package com.ls.example;

import com.ls.example.handler.FirstHttpServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author heartccace
 * @create 2020-04-27 13:15
 * @Description TODO
 * @Version 1.0
 */
public class FirstServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());
        pipeline.addLast("FirstHttpServerHandler", new FirstHttpServerHandler());
    }
}

