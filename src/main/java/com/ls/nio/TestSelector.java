package com.ls.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author heartccace
 * @create 2020-05-02 10:29
 * @Description TODO
 * @Version 1.0
 */
public class TestSelector {
    public static void main(String[] args) throws IOException {
        int ports[] = new int[5];
        ports[0] = 5678;
        ports[1] = 5679;
        ports[2] = 5680;
        ports[3] = 5681;
        ports[4] = 5682;

        Selector selector = Selector.open();

        for( int i = 0; i < 5; i++) {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            // ServerSocket socket = socketChannel.socket();
            socketChannel.bind(address);
            System.out.println("监听端口: " + ports[i]);

        }

        while(true) {
            int select = selector.select();
            System.out.println("select: " + select);
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if(selectionKey.isAcceptable() ) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel accept = serverSocketChannel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ);
                    iterator.remove();
                    System.out.println("获取到连接对象: " + accept);
                } else if(selectionKey.isReadable()) {
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    int readByte = 0;

                    while(true) {
                        ByteBuffer buffer = ByteBuffer.allocate(512);
                        buffer.clear();
                        int read = channel.read(buffer);
                        if(read <= 0) break;
                        buffer.flip();
                        channel.write(buffer);
                        readByte += read;
                        System.out.println("接收到客户端: " + channel + ", " + readByte + " 字节");
                    }
                    iterator.remove();
                }
            }
        }
    }
}
