package com.ls.nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author heartccace
 * @create 2020-05-05 22:29
 * @Description TODO
 * @Version 1.0
 */
public class Server {

    public static void main(String[] args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(8899);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
        socket.setReuseAddress(true);
        socket.bind(address);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(true) {
            SocketChannel accept = serverSocketChannel.accept();
            accept.configureBlocking(false);

            int count = 0;
            while(-1 != count) {

                count = accept.read(buffer);
                buffer.rewind();
            }

        }
    }
}
