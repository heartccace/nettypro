package com.ls.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author heartccace
 * @create 2020-04-30 12:18
 * @Description TODO
 * @Version 1.0
 */
public class FileCopy {
    private static final String commonPath = FileCopy.class.getResource("/").getPath();
    private static final String inputFilePath = commonPath + "/input.txt";
    private static final String outputFilePath = commonPath + "/output.txt";

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(inputFilePath);
        FileOutputStream fos = new FileOutputStream(outputFilePath);
        try{
            FileChannel inChannel = fis.getChannel();
            FileChannel outChannel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(4);
            while(true) {
                buffer.clear();
                int read = inChannel.read(buffer); //如果buffer中limit == position则无法继续读入
                if(read != 0  ) {
                    System.out.println(read);
                }

                if(read == -1) break;
                buffer.flip();
                outChannel.write(buffer);
                // buffer.flip();
            }
        } finally {
            fis.close();
            fos.close();
        }
    }
}
