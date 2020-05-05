package com.ls.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * @author heartccace
 * @create 2020-05-04 10:05
 * @Description 字符集编码
 * @Version 1.0
 */
public class NioTest05 {
    public static void main(String[] args) throws Exception {
        String path = NioTest05.class.getResource("/").getPath();
        String inputFile = path + "/NioTest05_in.txt";
        String outputFile = path + "/NioTest05_out.txt";

        RandomAccessFile inputRandomAccessFile = new RandomAccessFile(inputFile,"r");
        RandomAccessFile outputRandomAccessFile = new RandomAccessFile(outputFile, "rw");

        FileChannel inputChannel = inputRandomAccessFile.getChannel();
        FileChannel outputChannel = outputRandomAccessFile.getChannel();

        long fileLength = new File(inputFile).length();
        MappedByteBuffer byteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY,0,fileLength);

        Charset charset = Charset.forName("iso-8859-1");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        CharBuffer decode = decoder.decode(byteBuffer);
        ByteBuffer encode = encoder.encode(decode);

        System.out.println("============================");
        Charset.availableCharsets().forEach((k,v) -> {
            System.out.println(k + " , " +  v);
        });
        System.out.println("============================");

        outputChannel.write(encode);

        inputRandomAccessFile.close();
        outputRandomAccessFile.close();

    }
}
