package com.ls.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author liushuang
 * @create 2019-11-24 22:12
 */
public class Client {
    public static void main(String[] args) throws Exception{
        SocketChannel sc = SocketChannel.open();
        Selector selector = Selector.open();
        sc.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress("127.0.0.1",6666);
        if(!sc.connect(address)) {
            while (!sc.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞");
            }
        }
        String str ="Hello , server";
        ByteBuffer wrap = ByteBuffer.wrap(str.getBytes());
        sc.write(wrap);
        System.in.read();

    }
}

