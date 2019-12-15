package com.ls.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author liushuang
 * @create 2019-12-14 14:53
 */
public class MessageProtocolDecoder extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.readBytes(buffer);
        MessageProtocol message = new MessageProtocol(buffer, length);
        out.add(message);
    }
}
