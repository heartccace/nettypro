package com.ls.netty.connection;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author liushuang
 * @create 2019-12-09 20:30
 */
class LongConnection {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 使用HTTP编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 以块方式写，添加ChunkedWriteHandler
                            pipeline.addLast(new ChunkedWriteHandler());
                            /**
                             * 1.http数据在传输过程中是分段的,可以将多个段聚合
                             * 2.这就是为什么，当浏览器发送大量数据时，就会发送多次http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /**
                             * 1.对应的websocket数据传输使用帧
                             * 2. 对应不同的类型处理可以使用WebSocketfFrame
                             * 3. 浏览器请求ws:http://localhost:8080/hello
                             * 4. WebSocketServerProtocolHandler核心功能将http协议升级为ws协议，保持长连接
                             *
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            // 具体业务处理
                            pipeline.addLast(new TextWebSocketFrameHandler());

                        }
                    });
            ChannelFuture sync = bootstrap.bind(8080).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        System.out.println("------------------服务器已启动-----------------");
                    }
                }
            });
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {

        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
