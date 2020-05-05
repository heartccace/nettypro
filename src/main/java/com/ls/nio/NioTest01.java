package com.ls.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author heartccace
 * @create 2020-04-30 10:45
 * @Description TODO
 * @Version 1.0
 */
public class NioTest01 {
    public static void main(String[] args) throws Exception {
        String filePath = NioTest01.class.getResource("/file01.txt").getPath();
        FileInputStream inputStream = new FileInputStream(filePath);
        FileChannel channel = inputStream.getChannel();
        ByteBuffer buffers = ByteBuffer.allocate(512);

       channel.read(buffers);
        // 将读模式转换为写模式
        buffers.flip();

        while (buffers.remaining() > 0) {
            byte  b = buffers.get();
            System.out.println("character: " + (char)b);
        }
        inputStream.close();
    }
}
