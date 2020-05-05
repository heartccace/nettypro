package com.ls.nio;

import java.nio.ByteBuffer;

/**
 * @author heartccace
 * @create 2020-04-30 13:25
 * @Description slice方法截取的Buffer与原来的Buffer共享一段数据
 * @Version 1.0
 */
public class NioTest03 {
    public static void main(String[] args) {
        ByteBuffer buffer =ByteBuffer.allocate(10);
        ByteBuffer.allocateDirect(1024);
        for (int i = 0; i < buffer.capacity(); i ++) {
            buffer.put((byte) i);
        }
        buffer.position(2);
        buffer.limit(4);
        ByteBuffer sliceBuffer = buffer.slice();

        for (int i = 0; i < sliceBuffer.capacity(); ++i) {
            byte b = sliceBuffer.get(i);
            b +=2;
            sliceBuffer.put(i,b);
        }
        buffer.position(0);
        buffer.limit(buffer.capacity());
        while(buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }


    }
}
