package com.ls.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 异步执行写入操作
 * @author liushuang
 * @create 2019-11-27 22:54
 */
public class NettyServerHandler01 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("context------------>" + ctx);
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(((ByteBuf) msg).toString(CharsetUtil.UTF_8));
        System.out.println("收到客户地址：" + ctx.channel().remoteAddress());
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(1000 * 10);
                System.out.println("向客户端发送消息");
                ctx.writeAndFlush(Unpooled.copiedBuffer("HELLO,客户端", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ctx.channel().eventLoop().schedule(() -> {
            System.out.println("定时异步任务");
        },1000, TimeUnit.MINUTES);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("HELLO,客户端", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // ctx.channel().close();
        ctx.close();
    }
}
