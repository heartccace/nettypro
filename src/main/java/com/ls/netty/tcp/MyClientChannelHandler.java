package com.ls.netty.tcp;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author liushuang
 * @create 2019-12-14 15:23
 */
public class MyClientChannelHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String[] content= {
                "最灵繁的人也看不见自己的背脊",
                "最困难的事情就是认识自己",
                "有时候读书是一种巧妙地避开思考的方法",
                "阅读一切好书如同和过去最杰出的人谈话",
                "越是没有本领的就越加自命不凡",
                "知人者智，自知者明。胜人者有力，自胜者强",
                "意志坚强的人能把世界放在手中像泥块一样任意揉捏",
                "自己活着，就是为了使别人过得更美好",
                "要掌握书，莫被书掌握；要为生而读，莫为读而生",
                "要知道对好事的称颂过于夸大，也会招来人们的反感轻蔑和嫉妒"
        };
        for(int i = 0; i < 10; i++) {
            String sendMsg = content[(int) (Math.random() * 10)];
            byte[] bytes = sendMsg.getBytes();
            int length = bytes.length;
            MessageProtocol message = new MessageProtocol(bytes, length);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

    }
}
