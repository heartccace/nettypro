package com.ls.nio.chat;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author liushuang
 * @create 2019-11-25 20:04
 */
public class GroupServer {

    private ServerSocketChannel ssc;
    private Selector selector;

    public GroupServer() {
        try {
            // 创建channel
            this.ssc = ServerSocketChannel.open();
            // 绑定端口
            this.ssc.socket().bind(new InetSocketAddress(6666));
            // 配置为非阻塞
            this.ssc.configureBlocking(false);
            // 创建selector
            this.selector = Selector.open();
            // 将select注册到channel（一个selector可以注册多个channel）
            this.ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // 创建监听
    public void listen() {
        try{
            // 轮询监听
            while(true) {
                int count = this.selector.select(2000);
                // 获得连接
               if(count > 0) {
                   Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                   while(keys.hasNext()) {
                       SelectionKey key = keys.next();
                       // 对连接请求事件做处理
                       if(key.isAcceptable()) {
                           SocketChannel accept = this.ssc.accept();
                           accept.configureBlocking(false);
                           accept.register(this.selector, SelectionKey.OP_READ);
                           System.out.println("用户：" + accept.getRemoteAddress()+ "已上线");
                       }
                       // 对读事件做处理
                       if(key.isReadable()) {
                           // 处理聊天请求
                           this.readData(key);
                       }
                       // 将当前事件移除
                       keys.remove();
                   }
               } else {
                   System.out.println("waiting for connecting");
               }

            }
        }catch(IOException ex) {

        }
    }

    private void readData(SelectionKey selectionKey) {
        SocketChannel sc = null;
        try {
            // 获取当前事件channel
            sc = (SocketChannel)selectionKey.channel();
            // 分配缓存空间
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 将数据写入缓冲
            int count = sc.read(buffer);
            if(count > 0) {
                String msg = new String(buffer.array());
                // 消息转发功能实现
                sendInfoToOther(msg,sc);
                System.out.println("接受到客户端消息：" + msg);
            }

        } catch (IOException ex) {
            try {
                System.out.println(sc.getRemoteAddress() +"下线了");
                selectionKey.cancel();
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendInfoToOther(String msg, SocketChannel itself) throws IOException {
        // 遍历所有注册事件
        for (SelectionKey key: selector.keys()) {
            Channel targetChannel = key.channel();
            // 向所有非当前通道（channel）转发消息
            if(targetChannel instanceof  SocketChannel && targetChannel != itself) {
                ((SocketChannel) targetChannel).write(ByteBuffer.wrap(msg.getBytes()));
            }
        }
    }
    // 主线程运行
    public static void main(String[] args) {
        new GroupServer().listen();
    }
}
