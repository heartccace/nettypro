package com.ls.proto.client.handler;

import com.ls.proto.DataInfo;
import com.ls.proto.MyDataInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author heartccace
 * @create 2020-04-29 15:49
 * @Description TODO
 * @Version 1.0
 */
public class ProtoClientHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {
        System.out.println(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive and send message");
        MyDataInfo.MyMessage student = MyDataInfo.MyMessage.newBuilder()
                .setDataType(MyDataInfo.MyMessage.DataType.PersonType)
                .setAnimal(MyDataInfo.Animal.newBuilder()
                .setName("fish")
                .setAge(1).build()).build();
        ctx.channel().writeAndFlush(student);
    }
}
