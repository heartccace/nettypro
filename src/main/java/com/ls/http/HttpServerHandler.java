package com.ls.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 *
 * HttpObject客户端和服务器通话信息封装对象
 * @author liushuang
 * @create 2019-12-02 20:22
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof HttpRequest) {
            //对特定资源进行过滤
            HttpRequest request = (HttpRequest)msg;
            String uriStr = request.uri();
            URI uri = new URI(uriStr);
            if("/favicon.ico".equals(uri.getPath())) {
                System.out.println("不对/favicon.ico请求做处理");
                return;
            }
            System.out.println("客户端地址为：" + ctx.channel().remoteAddress());
            ByteBuf content = Unpooled.copiedBuffer("客户端你好，我是服务器", CharsetUtil.UTF_8);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain; charset=utf-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
