package com.ls.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author liushuang
 * @create 2019-11-24 19:39
 */
public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1",8900);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("hello".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
