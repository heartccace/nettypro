package com.ls.group;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;

import java.io.File;
import java.net.*;
import java.util.Enumeration;

/**
 * @author heartccace
 * @create 2020-07-21 20:40
 * @Description TODO
 * @Version 1.0
 */
public class Sender {
    private String host;
    private int port;
    // private File file;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    public Sender(String host,int port) {
        this.host = host;
        this.port = port;
        // this.file = file;
    }

    public void init() throws SocketException, InterruptedException, UnknownHostException {
        InetSocketAddress groupAddress = new InetSocketAddress(host,port);
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
        NioDatagramChannel ch = (NioDatagramChannel)bootstrap.bind(groupAddress.getPort()).sync().channel();
        ch.joinGroup(groupAddress.getAddress(),ni,InetAddress.getByName("127.0.0.1"));
        // ch.joinGroup(groupAddress,ni).sync();
        this.channel = ch;
        System.out.println("消费者准备就绪");
        ch.closeFuture().await();

    }

    public static void test() throws SocketException {
        InetAddress address = null;

        Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
        while (ni.hasMoreElements()) {
            NetworkInterface network = ni.nextElement();
            if(network.supportsMulticast() && !network.isLoopback()) {
                Enumeration<InetAddress> inetAddresses = network.getInetAddresses();
                while(inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if(inetAddress instanceof Inet4Address) {
                        System.out.println(network.getName());
                        address = inetAddress;
                    }
                }
            }

        }
    }

    public static void main(String[] args) throws SocketException {
        test();
        try {
            new Sender("239.255.27.1", 8899).init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
