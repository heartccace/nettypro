package com.ls.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * @author liushuang
 * @create 2019-11-24 19:16
 */
public class Server {

    public static void handleMessage(Socket socket) {
        InputStream inputStream = null;
        try {
             inputStream = socket.getInputStream();
            byte[] message = new byte[1024];
            while(true) {
                int read = inputStream.read(message);
                if( read != -1) {
                    System.out.println(new String(message,0,read));
                } else {
                    break;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8900);
        while(true) {
            System.out.println("waiting for connecting");
            final Socket accept = ss.accept();
            System.out.println("accept connecting");
            newCachedThreadPool().execute(new Runnable() {
                public void run() {
                    handleMessage(accept);
                }
            });

        }

    }
}
