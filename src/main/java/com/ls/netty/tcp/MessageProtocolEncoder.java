package com.ls.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author liushuang
 * @create 2019-12-14 15:12
 */
public class MessageProtocolEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        int length = msg.getLength();
        byte[] buffer = msg.getData();
        out.writeInt(length);
        out.writeBytes(buffer);
    }
}
