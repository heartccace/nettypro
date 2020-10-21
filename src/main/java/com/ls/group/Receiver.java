package com.ls.group;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

import java.io.File;
import java.net.*;
import java.util.Enumeration;

/**
 * @author heartccace
 * @create 2020-07-21 22:56
 * @Description TODO
 * @Version 1.0
 */
public class Receiver {
    private String host;
    private int port;
    // private File file;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private InetSocketAddress groupAddress;
    public Receiver(String host,int port) {
        this.host = host;
        this.port = port;
        this.groupAddress = new InetSocketAddress(host,port);
        // this.file = file;
    }

    public void init() throws SocketException, InterruptedException, UnknownHostException {
        InetSocketAddress groupAddress =new InetSocketAddress(host,port);
        InetAddress localAddress = null;
        NetworkInterface ni = NetUtil.LOOPBACK_IF;
        Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
        while(inetAddresses != null && inetAddresses.hasMoreElements()) {
            InetAddress address = inetAddresses.nextElement();
            if(address instanceof Inet4Address) {
                localAddress = address;
            }
        }
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channelFactory(()-> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                .localAddress(localAddress,groupAddress.getPort())
                .option(ChannelOption.IP_MULTICAST_IF,ni)
                .option(ChannelOption.SO_REUSEADDR,true)
                .handler(new ChannelHandlerInitializer());
        NioDatagramChannel ch  = (NioDatagramChannel)bootstrap.bind(groupAddress.getPort()).sync().channel();
        ch.joinGroup(groupAddress,ni);

        this.channel = ch;
        System.out.println("生产者准备就绪");
       // ch.closeFuture().sync();
    }

    public void send(String msg) throws InterruptedException {
        if(this.channel != null) {
            channel.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                    groupAddress)).sync();
        }
    }

    public static void main(String[] args) {
        Receiver receiver = new Receiver("239.255.27.1", 8899);
        try {
            receiver.init();
            receiver.send("hello");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
