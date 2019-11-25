package com.ls.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liushuang
 * @create 2019-11-24 21:42
 */
public class Server {
    public static void main(String[] args) throws  Exception{
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector selector = Selector.open();
        ssc.socket().bind(new InetSocketAddress(6666));
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            if(selector.select(1000) == 0) {
                System.out.println("服务器等待了一秒，无连接");
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator != null && keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if(key.isAcceptable()) {
                    SocketChannel accept = ssc.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    ByteBuffer  byteBuffer = (ByteBuffer) key.attachment();
                    channel.read(byteBuffer);
                    System.out.println("接收到客户端消息:" + new String(byteBuffer.array()));
                }
                keyIterator.remove();
            }
        }


    }
}
