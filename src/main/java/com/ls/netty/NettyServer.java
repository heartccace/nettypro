package com.ls.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author liushuang
 * @create 2019-11-27 22:01
 */
public class NettyServer {
    public static void main(String[] args) throws Exception{
       EventLoopGroup bossGroup = new NioEventLoopGroup();
       EventLoopGroup workGroup = new NioEventLoopGroup();
       try{
           ServerBootstrap bootstrap = new ServerBootstrap();
           bootstrap.group(bossGroup,workGroup) //设置两个线程组
                   .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器通道的实现
                   .option(ChannelOption.SO_BACKLOG,128) // 设置线程队列得到连接个数
                   .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                   .childHandler(new ChannelInitializer<SocketChannel>() {
                       // 给pipeLine设置处理器
                       @Override
                       protected void initChannel(SocketChannel socketChannel) throws Exception {
                           socketChannel.pipeline().addLast(new NettyServerHandler());
                       }
                   }); // 给workGroup的event设置对应的管理
           System.out.println(".....服务器 is ready...");
           ChannelFuture future = bootstrap.bind(8088).sync();
           if(future.isSuccess()) {
               System.out.println("启动服务器成功");
           } else {
               System.out.println("启动服务器失败");
           }
           // 对关闭通道进行监听
           future.channel().closeFuture().sync();
       }finally {
           bossGroup.shutdownGracefully();
           workGroup.shutdownGracefully();
       }


    }
}
