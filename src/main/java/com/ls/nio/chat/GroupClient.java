package com.ls.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author liushuang
 * @create 2019-11-25 21:02
 */
public class GroupClient {

    private SocketChannel sc;
    private Selector selector;
    private String username;

    public GroupClient() {
        try{
            // 初始化 channel
            this.sc = SocketChannel.open(new InetSocketAddress("127.0.0.1",6666));
            // 设置非阻塞
            this.sc.configureBlocking(false);
            // 初始化selector
            this.selector = Selector.open();
            // 将channel注册到selector
            this.sc.register(this.selector, SelectionKey.OP_READ);
            // 初始化用户名
            this.username = this.sc.getLocalAddress().toString().substring(1);
        } catch(IOException ex) {}
    }

    /**
     * 发送消息
     * @param msg 消息内容体
     */
    public void sendData(String msg) {
        msg = this.username + " 说：" + msg;
        try {
            this.sc.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException ex) {

        }
    }

    /**
     * 读取消息
     */
    public void readMsg() {
        try {
            int count = this.selector.select();
            if(count > 0) {
                // 获取所有发生的监听事件
                Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    // 监听读事件
                    if(key.isReadable()) {
                        // 获取当前事件的channel
                        SocketChannel channel =(SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        String msg = new String(buffer.array());
                        System.out.println(msg);
                    }
                    // 移除当前事件
                    keys.remove();
                }

            } else {
                //System.out.println("没有可用的通道。。。");
            }

        } catch (IOException ex) {

        }
    }

    public static void main(String[] args) {
        final GroupClient client = new GroupClient();
        // 循环监听当前事件（每隔3s）
        new Thread() {
            @Override
            public void run() {
               while (true) {
                   client.readMsg();
                   try{
                       Thread.sleep(3000);
                   }catch (InterruptedException ex) {
                       ex.getMessage();
                   }
               }
            }
        }.start();
        // 获取系统输入
        Scanner scanner = new Scanner(System.in);
        // 循环输入
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            client.sendData(msg);
        }
    }
}
