package stickybag;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author heartccace
 * @create 2020-05-13 20:00
 * @Description TODO
 * @Version 1.0
 */
public class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(new StringEncoder());
//        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new DefaultChannelHandler());
    }
}
