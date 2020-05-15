package com.ls.nio.zerocopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLOutput;

/**
 * @author heartccace
 * @create 2020-05-05 22:45
 * @Description TODO
 * @Version 1.0
 */
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel =SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("localhost",8899));

        socketChannel.configureBlocking(false);

        String fileName = "G:\\BaiduNetdiskDownload\\094  每特学院Java高端培训系列视频教程\\copy.rar";

        FileChannel channel = new FileInputStream(fileName).getChannel();
        long startTime = System.currentTimeMillis();

        channel.transferTo(0 , channel.size(),socketChannel);

        long endTime = System.currentTimeMillis();

        System.out.println("传输字节数: " + channel.size()+ "花费时间:  " + (endTime - startTime));


    }
}
