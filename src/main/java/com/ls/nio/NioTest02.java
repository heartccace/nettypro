package com.ls.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author heartccace
 * @create 2020-04-30 11:23
 * @Description TODO
 * @Version 1.0
 */
public class NioTest02 {
    public static void main(String[] args) throws Exception {
        String filePath = NioTest02.class.getResource("/").getPath();
        FileOutputStream fos = new FileOutputStream(filePath + "/file03.txt");
        FileChannel channel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(512);
        byte[] message = "hello world welcome".getBytes();
        for(int i = 0; i < message.length; i++) {
            buffer.put(message[i]);
        }
        buffer.flip();
        channel.write(buffer);
        fos.close();
    }
}
