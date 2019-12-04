package com.ls.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * @author liushuang
 * @create 2019-12-02 20:25
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 添加http编/解码器处理器
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
//        pipeline.addLast("decoder", new StringDecoder(Charset.forName("utf-8")));
//        pipeline.addLast("encoder", new StringEncoder(Charset.forName("utf-8")));
        pipeline.addLast("HttpServerHandler", new HttpServerHandler());
        System.out.println("ok~~~~");
    }
}
