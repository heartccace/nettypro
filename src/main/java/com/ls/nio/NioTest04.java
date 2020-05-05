package com.ls.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author heartccace
 * @create 2020-04-30 14:37
 * @Description TODO
 * @Version 1.0
 */
public class NioTest04 {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.socket().bind(new InetSocketAddress(8899));
        SocketChannel accept = socketChannel.accept();
        int maxLength = 2 + 3 + 4;
        ByteBuffer buffers[] = new ByteBuffer[3];
        buffers[0] = ByteBuffer.allocate(2);
        buffers[1] = ByteBuffer.allocate(3);
        buffers[2] = ByteBuffer.allocate(4);
        while(true) {
            int byteRead = 0;
            while(byteRead < maxLength) {
                long r = accept.read(buffers);
                byteRead += r;
                System.out.println("byteRead: " + byteRead);
                Arrays.asList(buffers).stream().map(buffer -> "limit: " + buffer.limit() + "  position: " + buffer.position())
                .forEach(System.out::println);
            }
            Arrays.asList(buffers).forEach(buffer -> {
                buffer.flip();
            });
            long byteWrite = 0;
            while(byteWrite < maxLength) {
                long w = accept.write(buffers);
                byteWrite += w;
            }
            Arrays.asList(buffers).forEach(buffer -> {
                buffer.clear();
            } );
        }

    }
}
