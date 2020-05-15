package stickybag;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * @author heartccace
 * @create 2020-05-13 20:01
 * @Description TODO
 * @Version 1.0
 */
public class DefaultChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        String message = new String(buffer, Charset.forName("utf-8"));
        System.out.println("服务端接收到的消息内容:"+message);
        ctx.writeAndFlush("");
        ctx.channel().writeAndFlush("");
    }
}
