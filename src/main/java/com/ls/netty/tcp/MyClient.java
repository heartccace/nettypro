package com.ls.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author liushuang
 * @create 2019-12-14 14:41
 */
public class MyClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new MyClientChannelInitializer());
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8089)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            ex.getMessage();
        } finally {
            group.shutdownGracefully();
        }
    }
}
