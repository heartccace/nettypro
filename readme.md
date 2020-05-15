# Netty

Netty是异步事件驱动的网络应用程序框架，用于快速开发可维护的高性能协议服务器和客户端。

## Features

### Design

- 对于不同的传输类型统一API，阻塞或者非阻塞的socket
- 基于灵活且可扩展的事件模型，可将关注点明确分离
- 高度可定制的线程模型-单线程，一个或多个线程池，例如SEDA（staged event driven architecture）阶段性的事件驱动架构
- 真正的无连接数据报套接字支持（从3.1开始）

### performance

- 高吞吐量，低延迟
- 更少的资源消耗
- 最小化内存拷贝

### 核心组件

```
1、io.netty.channel.ChannelInitializer
2、io.netty.channel.ChannelHandler
```



# 一、 线程模型

### IO中流的分类 
    根据字节分: 分为字节流和字符流
    根据流类型分：输入流和输出流
    根据功能：节点流和过滤流（对流进行过滤）
### 传统阻塞BIO模型

​		传统的网络编程IO模型采用的时阻塞IO，通常的处理方式是一个客户端在服务器端对应一个线程。

当客户端数量增多时，线程数量增多。当线程到达一定数量后会导致系统性能下降。或许可以采用线程池来处理，但是请求数量多的情况下依然无法有较好的性能。

​		传统网络编程的阻塞点在ServerSocket的accept方法和InputStream的read方法，如果一直未接收到来自客户端的连接，线程资源就会一直被浪费。

​	BIO是面向流进行编程的

### NIO非阻塞模型

​		NIO（Non-Block IO）非阻塞编程，面向块编程。采用selector、buffer、channel进行数据传输。一个selector可以注册多个channel，channel直接操作buffer对数据进行读写。

NIO采用事件驱动模型。NIO通过selector进行事件注册，由selector轮询注册事件，当事件发生直接交由线程处理。一个线程对应一个selector，一个selector可以对应多个事件，一个selector可以处理多个channel，每个channel对应一个buffer。

NIO基于IO多路复用模型：多个连接复用一个阻塞对象

JAVA nio中拥有三个核心概念selector、channel、和Buffer，在nio中是面向块（block）或者缓冲区（buffer）编程的。buffer本身就是一块内存，底层实际上是个数组，数据的读写都是通过buffer来实现的。

channel指的是可以向其写入数据或者是从中读取数据的对象。

所有的数据读写都是通过buffer来进行的，永远不会出现直接向channel写入数据的情况，或者直接从channel读取数据的情况

与strem不同的是，channel是双向的，一个流只能是inputstream或者outputstream



```
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
```



buffer的slice()方法会截取position 到limit的位置的buffer，该buffer与原来的buffer共享同一段数据，但是position、limit和capcity各自独立。



#### seletor

selector是一个可选channel对象的多路复用。selector可以通过Selector的open方法创建，该方法将使用系统默认的java.nio.channels.spi.SelectorProvider去创建一个新的selector。selector也可以通过调用自定义实现java.nio.channels.spi.SelectorProvider类的openSelector方法。selector可以通过调用colse方法进行关闭。

一个可选择的channel是通过SelectionKey对象注册到selector。一个selector维护三种可选key的集合。

- key set包含代表这当前channel注册到这个selector的所有key，通过keys()方法可以获得
- selected-key代表当前感兴趣的key，是key set的子集，通过selectedKeys()获取。
- cancelled-key 也是key set的一个子集，此时channel并没有取消注册

在selector创建时，这三个key都是空的。

## Reactor模型

netty整体架构是reactor模式的整体体现。

Reactor：Reactor在一个单独的线程中运行，负责监听和事件分发，分发给适当的处理程序来对IO事件做出反应，他就像公司的接线员，他接听来自客户端的电话并将线路转移到适当的联系人。

Handlers： 处理程序执行I/O事件要完成的实际事件，类似于客户想要与交谈的公司中的实际官员，Reactor通过适当的处理程序来响应I/O事件，处理程序执行非阻塞操作。

#####  单Reactor单线程

 	1. Selector是I/O复用模型的标准网络编程API，可以实现应用程序通过一个阻塞对象监听多路链接请求
 	2. Reactor对象通过Selector监控客户端请求事件，收到事件后通过Dispatch进行分发
 	3. 如果是建立连接请求事件，则由Acceptor通过Accept处理连接请求，然后创建一个Handler对象处理连接完成后的后续业务。
 	4. 如果不是建立连接事件，则Reactor会分发调用相应的Handler来响应
 	5. Handler会完成Read—>业务处理 —>Send的完整业务流程

###### 优缺点

优点

1. ​	模型简单，没有多线程、进程通信、竞争的问题全部都在一个线程中完成		

缺点

1. 性能问题，只有一个线程，无法安全发挥多核CPU的性能，Handler在处理某个连接上的业务时，整个进程无法处理其他连接事件，很容易导致性能瓶颈
2. 可靠性问题，线程意外终止，或者进入死循环，会导致整个系统通信模块不可用，不能够接收和处理外部消息。造成节点故障。

使用场景： 客户端的数量有限，业务处理非常快捷，比如Redis在业务处理的事件复杂O(1)的情况

###### 单Reactor多线程

采用一个Reactor监听，处理数据采用多线程。

###### 主从Reactor多线程

​    

### 字符编码

ASCII 采用7个bit表示一个字符，共计128种字符

ISO-8859-1采用8bit表示一个字符，共计256种

GB2312 两个字节表示一个汉字

GBK支持生僻字 是GB2312的父集

gb10030支持所有的汉字

big5台湾繁体

unicode，采用两个字节表示一个字符（统一了编码）

UTF，unicode translation format

unicode是一种编码方式，而UTF则是一种存储方式，UTF-8是unicode的实现方式之一。

BOM（byte order mark）: 

### 零拷贝(zero-copy)

#### What Is Zero-Copy?

为了更好地理解问题的解决方案，我们首先需要了解问题本身。让我们看一下网络服务器通过网络将文件中存储的数据提供给客户端的简单过程所涉及的内容。这是一些示例代码：

```
read(file, tmp_buf, len);
write(socket, tmp_buf, len);
```

看起来很简单；您会认为只有这两个系统调用没有太多开销。实际上，这离事实还远。在这两个调用之后，数据已被至少复制了四次，并且执行了几乎相同数量的用户/内核上下文切换。 （实际上，此过程要复杂得多，但我想保持简单）。为了更好地了解所涉及的过程，请看一下图1。顶侧显示上下文切换，底侧显示复制操作。



![](/zero-copy.jpg)

图1.两个示例系统调用复制过程

第一步：读取系统调用导致上下文从用户模式切换到内核模式。第一个副本由DMA引擎执行，该引擎从磁盘读取文件内容并将其存储到内核地址空间缓冲区中。

第二步：将数据从内核缓冲区复制到用户缓冲区，然后读取的系统调用返回。调用返回导致上下文从内核切换回用户模式。现在，数据存储在用户地址空间缓冲区中，并且可以重新开始。

第三步：写系统调用导致上下文从用户模式切换到内核模式。执行第三次复制以再次将数据放入内核地址空间缓冲区。但是，这次将数据放入另一个缓冲区中，该缓冲区专门与套接字关联。

第四步：write系统调用返回，创建我们的第四个上下文切换。独立且异步地，当DMA引擎将数据从内核缓冲区传递到协议引擎时，发生第四次复制。您可能会问自己：“独立和异步是什么意思？通话返回之前是否传输了数据？”实际上，回叫并不能保证传输。它甚至不能保证传输的开始。这仅表示以太网驱动程序在其队列中具有免费的描述符，并已接受我们的数据进行传输。在我们之前可能有许多数据包排队。除非驱动程序/硬件实现优先级环或队列，否则将以先进先出的方式传输数据。 （图1中的分叉DMA副本说明了可以延迟最后一个副本的事实）。

消除拷贝的一种方法是跳过调用read，而是调用mmap。例如：

```
tmp_buf = mmap(file, len);
write(socket, tmp_buf, len);
```

为了更好地了解所涉及的过程，请看一下图2。上下文切换保持不变。







图2.调用mmap 

第一步：mmap系统调用使DMA引擎将文件内容复制到内核缓冲区中。然后与用户进程共享缓冲区，而无需在内核和用户内存空间之间执行任何复制。

第二步：写系统调用使内核将数据从原始内核缓冲区复制到与套接字关联的内核缓冲区中。

第三步：第三次复制发生在DMA引擎将数据从内核套接字缓冲区传递到协议引擎时。

通过使用mmap而非read，我们减少了内核必须复制的数据量的一半。当传输大量数据时，这会产生相当好的结果。但是，这种改进并非没有代价。使用mmap + write方法时存在隐患。当您在内存中映射文件时，当另一个进程将同一个文件截断时，您将调用write方法。总线错误信号SIGBUS将中断您的写入系统调用，因为您执行了错误的内存访问。该信号的默认行为是杀死进程并转储核心，这不是网络服务器最理想的操作。有两种方法可以解决此问题。





自我总结：

传统拷贝: 传统拷贝是在用户空间和内核空间传输数据进行拷贝。首先由用户空间发送read系统调用，从用户空间切换到内核空间，内核空间通过DMA(直接内存访问)，将硬盘上的数据拷贝到内核缓存，read方法返回，从内核空间切换回用户空间，数据由内核缓存拷贝到用户缓存，用户空间发送write系统调用，由用户空间切换到内核空间，数据由用户空间拷贝到内核缓存（此处的缓存和上一次的内核缓存不一样），内核缓存将数据再拷贝到协议引擎，write方法返回，由内核空间切换回用户空间，总共由四次空间切换和四次内存拷贝。

零拷贝：零拷贝是用户发送sendfile系统调用，内核空间通过DMA直接将硬盘数据拷贝到内核缓存，当文件进行传送的时候，通过DMA将内核缓存的信息（缓存的地址，缓存的大小）通过CUP copy到socket buffer，最后通过DMA将数据发送到协议引擎。

### EventLoopGroup（Executor执行器是核心，执行具体线程）

eventLoopGroup继承自EventExecutorGroup，是一种特殊的EventExecutorGroup，他的作用是在事件循环当中，在进行selection的时候，可以注册channel。

```
public interface EventLoopGroup extends EventExecutorGroup {
    /**
     * Return the next {@link EventLoop} to use
     */
    @Override
    EventLoop next();

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The returned {@link ChannelFuture}
     * will get notified once the registration was complete.
     */
    ChannelFuture register(Channel channel);

    /**
     * Register a {@link Channel} with this {@link EventLoop} using a {@link ChannelFuture}. The passed
     * {@link ChannelFuture} will get notified once the registration was complete and also will get returned.
     */
    ChannelFuture register(ChannelPromise promise);

    /**
     * Register a {@link Channel} with this {@link EventLoop}. The passed {@link ChannelFuture}
     * will get notified once the registration was complete and also will get returned.
     *
     * @deprecated Use {@link #register(ChannelPromise)} instead.
     */
    @Deprecated
    ChannelFuture register(Channel channel, ChannelPromise promise);
}
```

EventExecutorGroup通过使用next方法提供事件执行器，除此之外，它还负责处理事件执行器的生命周期，并且以全局的方式对事件执行器的关闭。

```
public interface EventExecutorGroup extends ScheduledExecutorService, Iterable<EventExecutor> {

    /**
     * Returns {@code true} if and only if all {@link EventExecutor}s managed by this {@link EventExecutorGroup}
     * are being {@linkplain #shutdownGracefully() shut down gracefully} or was {@linkplain #isShutdown() shut down}.
     */
    boolean isShuttingDown();

    /**
     * Shortcut method for {@link #shutdownGracefully(long, long, TimeUnit)} with sensible default values.
     *
     * @return the {@link #terminationFuture()}
     */
    Future<?> shutdownGracefully();

    /**
     * Signals this executor that the caller wants the executor to be shut down.  Once this method is called,
     * {@link #isShuttingDown()} starts to return {@code true}, and the executor prepares to shut itself down.
     * Unlike {@link #shutdown()}, graceful shutdown ensures that no tasks are submitted for <i>'the quiet period'</i>
     * (usually a couple seconds) before it shuts itself down.  If a task is submitted during the quiet period,
     * it is guaranteed to be accepted and the quiet period will start over.
     *
     * @param quietPeriod the quiet period as described in the documentation
     * @param timeout     the maximum amount of time to wait until the executor is {@linkplain #shutdown()}
     *                    regardless if a task was submitted during the quiet period
     * @param unit        the unit of {@code quietPeriod} and {@code timeout}
     *
     * @return the {@link #terminationFuture()}
     */
    Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

    /**
     * Returns the {@link Future} which is notified when all {@link EventExecutor}s managed by this
     * {@link EventExecutorGroup} have been terminated.
     */
    Future<?> terminationFuture();

    /**
     * @deprecated {@link #shutdownGracefully(long, long, TimeUnit)} or {@link #shutdownGracefully()} instead.
     */
    @Override
    @Deprecated
    void shutdown();

    /**
     * @deprecated {@link #shutdownGracefully(long, long, TimeUnit)} or {@link #shutdownGracefully()} instead.
     */
    @Override
    @Deprecated
    List<Runnable> shutdownNow();

    /**
     * Returns one of the {@link EventExecutor}s managed by this {@link EventExecutorGroup}.
     */
    EventExecutor next();

    @Override
    Iterator<EventExecutor> iterator();

    @Override
    Future<?> submit(Runnable task);

    @Override
    <T> Future<T> submit(Runnable task, T result);

    @Override
    <T> Future<T> submit(Callable<T> task);

    @Override
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    @Override
    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}
```

EventLoop一旦被注册将会处理channel所有的I/O操作，一个EventLoop实例，将会处理超过一个channel，但是这取决于它的内部实现细节。

```
public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}

```

EventExecutor是一种特殊的EventExecutorGroup，带有一些方便的方法来查看线程在事件循环中是否被执行，出此次之外它扩展了EventExecutorGroup，允许通用方法的访问方式。

结论： EventLoop是EventExcutor的子类。



### Channel

Channel是对network socket或者能够进行I/O操作(例如读、写、连接、绑定)的组件的联系。

一个channel提供用户以下功能：

- 当前channel的状态（开启还是关闭）
- channel的配置参数(例如接收缓冲区大小)
- channel支持的I/O操作(例如读、写、连接、绑定)
- 当前channel相关联负责处理所有I/O事件和请求的channelPipiline

#### 所有的I/O操作都是异步的

netty中所有的I/O操作都是异步的。这意味着任何I/O调用都会立即返回，并不保证调用结束后请求的I/O操作已经完成。你将会被返回一个channelFuture实例，在I/O操作成功、失败、或者取消该channelFuture都会通知你。

#### channel的层次性

一个channel可以有一个父亲，取决于它的创建方式，对于一个SocketChannel实例而言，它被ServerSocketChannel,所接受，将会返ServerSocketChannel作为其父channel，通过调用parent()方法。层次结构的语义取决于Channel所属的传输实现

#### Netty处理器：

1、netty的处理器可以分为：入栈处理器和出栈处理器

2、入栈处理器的顶层是ChannelInboundHandler，出栈处理器的顶层是ChannelOutBoundHandler

3、数据处理时常用的各种编解码器本质上都是处理器

4、编节码器：无论我们向网络中发送什么数据，其本质都是由字节流的方式传输，将数据由原本的形式转化为字节流成为编码(encode)，将字节流转换为它原本格式称为节码(decode)

5、编码本质是一种出栈处理器：因此编码一定是ChannelOutboundHandler

6、解码本质是一种入栈处理器：因此解码一定是ChannelInboundHandler

7、在netty中，编码通常以xxxEncoder命名，解码以xxxDDecoder

#### 编解码器：（MessageToByteEncoder/ByteToMessageEncoder）

1、无论编码器还是解码器，其所接收的消息类型必须要与待处理的参数类型一直，否则该编码器或解码器并不会被执行

2、在解码器进行数据解码时，一定要判断缓冲(ByteBuf)中的数据是否足够，否则会产生一些问题

##### ReplayingDecoder

ReplayingDecoder是ByteToMessageDecoder的一个特殊变种，实现了 在阻塞I/O中非阻塞的解码范例。

ReplayingDecoder与ByteToMessageDecode最大的不同在于ReplayingDecoder 允许你实现decode() 和decodeLast() 方法，就像所有的字节早已收到一样，而不是检查需要字节的可用数量，列如，以下是ByteToMessageDecoder 的实现：

```
public class IntegerHeaderFrameDecoder extends ByteToMessageDecoder {
  
      @Override
     protected void decode(ChannelHandlerContext ctx,
                             ByteBuf buf, List<Object> out) throws Exception {
  
       if (buf.readableBytes() < 4) {
          return;
       }
  
       buf.markReaderIndex();
       int length = buf.readInt();
  
       if (buf.readableBytes() < length) {
          buf.resetReaderIndex();
          return;
       }
  
       out.add(buf.readBytes(length));
     }
   }
```

采用ReplayingDecoder的实现：

```
 public class IntegerHeaderFrameDecoder
        extends ReplayingDecoder<Void> {
  
     protected void decode(ChannelHandlerContext ctx,
                             ByteBuf buf) throws Exception {
  
       out.add(buf.readBytes(buf.readInt()));
     }
   }
```

##### ReplayingDecoder是如何运行的

ReplayingDecoder 通过一个特殊的ByteBuf实现，这个ByteBuf会抛出一个确定类型的Error，当在buffer中没有足够的数据的时候。在上面IntegerHeaderFrameDecoder，你只需要假设当你调用 buf.readInt()的时候，在buffer里面会有4个或者更多的字节。如果真的有4个字节在buffer中，它将会如你期望的返回整型头。否则，Error将会被抛出，控制权将会交还给ReplayingDecoder。如果ReplayingDecoder捕获异常，接下来它会重置buffer的读索引到它初始的位置（比如buffer的起始位置）当有更多数据到达buffer时再次调用decode（...）方法。

##### ReplayingDecoder的限制

ReplayingDecoder 简化了开发，相应就会有一些强制的限制

1. 一些buffer操作会被禁止

2. 如果网络慢或者消息格式不像以上那么简单，性能会很糟，解码器可能会不停的解码消息的同一部份

3. 你必须记住decode(..) 方法会被调用许多次去解码同一条消息，比如以下的列子将（可能）不会被正常执行：

   ```
   
    public class MyDecoder extends ReplayingDecoder<Void> {
     
        private final Queue<Integer> values = new LinkedList<Integer>();
     
         @Override
        public void decode(.., ByteBuf buf, List<Object> out) throws Exception {
     
          // Revert the state of the variable that might have been changed
          // since the last partial decode.
          values.clear();
     
          // A message contains 2 integers.
          values.offer(buf.readInt());
          values.offer(buf.readInt());
     
          // Now we know this assertion will never fail.
          assert values.size() == 2;
          out.add(values.poll() + values.poll());
        }
      }
   ```

   不被正常执行的原因可能会由于网络，导致同一条消息多次调用decode方法，由于ReplayingDecoder自定义了异常捕获，当一个字节到的时候values的size不会是2，多次往values里面放值，导致断言失败，无法执行以下流程。解决方案：

   ```
    public class MyDecoder extends ReplayingDecoder<Void> {
     
        private final Queue<Integer> values = new LinkedList<Integer>();
     
         @Override
        public void decode(.., ByteBuf buf, List<Object> out) throws Exception {
     
          // Revert the state of the variable that might have been changed
          // since the last partial decode.
          values.clear(); // 每次进入方法，事先清空values队列
     
          // A message contains 2 integers.
          values.offer(buf.readInt());
          values.offer(buf.readInt());
     
          // Now we know this assertion will never fail.
          assert values.size() == 2;
          out.add(values.poll() + values.poll());
        }
      }
   ```

   

```
// 定义枚举
   public enum MyDecoderState {
     READ_LENGTH,
     READ_CONTENT;
   }
  
   public class IntegerHeaderFrameDecoder
        extends ReplayingDecoder<MyDecoderState> {
  
     private int length;
  
     public IntegerHeaderFrameDecoder() {
       // Set the initial state.
       super(MyDecoderState.READ_LENGTH); // 设置初始化状态
     }
  
      @Override
     protected void decode(ChannelHandlerContext ctx,
                             ByteBuf buf, List<Object> out) throws Exception {
       switch (state()) {
       case READ_LENGTH:  // 读取头状态
         length = buf.readInt();
         checkpoint(MyDecoderState.READ_CONTENT); // 重置状态为读取内容状态
       case READ_CONTENT:
         ByteBuf frame = buf.readBytes(length); // 读取内容
         checkpoint(MyDecoderState.READ_LENGTH); // 重新重置状态
         out.add(frame);
         break;
       default:
         throw new Error("Shouldn't reach here.");
       }
     }
   }
```



##### netty内置编解码器

1.  DelimiterBasedFrameDecoder
2.  FixedLengthFrameDecoder
3. LengthFieldBasedFrameDecoder
4. LineBasedFrameDecoder

#### channelPipeLine

channelPipe的创建是在AbstractChannel里面创建

```
// AbstractChannel
protected DefaultChannelPipeline newChannelPipeline() {
    return new DefaultChannelPipeline(this);
}
```

所以在Channel里面维护了一个channelPipeLine，同时在ChannelPipeLine里面野维护了一个Channel对象。

在channelPipeLine的默认实现DefaultChannelPipeline中，维护了一个列表对象ChannelHandlerContext。



### ChannelPipeLine

ChannelPipeLine是ChannelHandlers的一个列表，ChannelHandlers负责处理或者拦截channel的inbound 事件和outbound 操作，ChannelPipeline 实现了一种高级的拦截器过滤器设计模式，提供用户完全控制事件的处理和channelHandler之间的相互作用

```
                                      I/O Request
                                              via Channel or
                                          ChannelHandlerContext
                                                        |
    +---------------------------------------------------+---------------+
    |                           ChannelPipeline         |               |
    |                                                  \|/              |
    |    +---------------------+            +-----------+----------+    |
    |    | Inbound Handler  N  |            | Outbound Handler  1  |    |
    |    +----------+----------+            +-----------+----------+    |
    |              /|\                                  |               |
    |               |                                  \|/              |
    |    +----------+----------+            +-----------+----------+    |
    |    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
    |    +----------+----------+            +-----------+----------+    |
    |              /|\                                  .               |
    |               .                                   .               |
    | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
    |        [ method call]                       [method call]         |
    |               .                                   .               |
    |               .                                  \|/              |
    |    +----------+----------+            +-----------+----------+    |
    |    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
    |    +----------+----------+            +-----------+----------+    |
    |              /|\                                  |               |
    |               |                                  \|/              |
    |    +----------+----------+            +-----------+----------+    |
    |    | Inbonbund Handler  1  |            | Outbound Handler  M  |    |
    |    +----------+----------+            +-----------+----------+    |
    |              /|\                                  |               |
    +---------------+-----------------------------------+---------------+
                    |                                  \|/
    +---------------+-----------------------------------+---------------+
    |               |                                   |               |
    |       [ Socket.read() ]                    [ Socket.write() ]     |
    |                                                                   |
    |  Netty Internal I/O Threads (Transport Implementation)            |
    +-------------------------------------------------------------------+
    
   ChannelPipeline p = ...;
   p.addLast("1", new InboundHandlerA());
   p.addLast("2", new InboundHandlerB());
   p.addLast("3", new OutboundHandlerA());
   p.addLast("4", new OutboundHandlerB());
   p.addLast("5", new InboundOutboundHandlerX())
```

pipeLine的执行顺序：

Inbonbund Handler在处理Inbonbund 事件的时候执行顺序是1 2 5

Outbound Handler在处理Outbound操作的时候执行顺序是 5 4 3



ChannelPipeLine是一个容器，存放一个一个ChannelHandlerContext对象，ChannelHandlerContext对象里面存放着我们所编写的或者netty提供的channelHandler对象。

#### ChannelHandlerContext

ChannelHandlerContext是channel和ChannelPipeLine桥梁和纽带，

channelHandler可以被添加到channelpipeline多次，相应会生成多个ChannelHandlerContext

### ServerBootstrap

ServerBootstrap是ServerChannel的一个子类，用于简化启动流程。

ServerChannel是netty的一个标记接口

```
/**
	接收进来的连接，尝试创建它的子channel通过接受连接
 * A {@link Channel} that accepts an incoming connection attempt and creates
 * its child {@link Channel}s by accepting them.  {@link ServerSocketChannel} is
 * a good example.
 */
public interface ServerChannel extends Channel {
    // This is a tag interface.
}
```



### java concurrent package

```
// 负责在需要时创建线程，通常其实现类都会维护一个ThreadGroup用于存放线程
public interface ThreadFactory {

    /**
     * Constructs a new {@code Thread}.  Implementations may also initialize
     * priority, name, daemon status, {@code ThreadGroup}, etc.
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread, or {@code null} if the request to
     *         create a thread is rejected
     */
    Thread newThread(Runnable r);
}
```

```
// 一个执行所提交的Runnable任务，将任务的提交和任务的执行解耦
public interface Executor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
```

```
// netty的线程处理执行器
public final class ThreadPerTaskExecutor implements Executor {
    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}

```

#### Future接口

Netty对java并发包下的Future进行了封装，原生Future接口完成isDone方法：返回true，如果任务完成。完成可能由于正常终止、发生异常或者被取消--在这些情况之中，这个方法将会返回true。Netty封装的Future，新增了isSuccess，完善了正常完成的情况。

Netty ChannelFuture建议使用addListener方法，而不是await





### reactor

reactor设计模式处理从应用程序一个或多个客户端并发发送的请求。应用程序中的每一个服务可能由几个方法组成和由一个单独事件处理器所表示，事件处理器负责分发特定的服务请求。事件处理器的分发是由初始化调度器执行，它管理着事件处理器的注册。多路服务请求由多路同步事件执行。



### Reactor模式角色构成（Reactor模式一共有5中角色构成）：

1. Handle（句柄或是描述符）：本质上是一种资源，有操作系统提供的；该资源表示一个个事件，比如说文件描述符，或是针对网络编程的Socket描述符。事件既可以来自于内部，也可以来自于内部；外部事件例如客户端的连接请求，客户端的数据等；内部事件例如操作系统的定时器事件等。他本质上一个文件描述符。Handle是事件发生的发源地。
2. Synchronous Event Demultiplexer(同步事件分离器)：它本身是一个系统的调用，用于等待事件的发生（事件可以有一个，也可以有多个）。调用方法在调用它的时候回被阻塞，一直阻塞到同步事件分离器产生事件位置。对于Linux系统来说，同步事件指的就是常用的I/O多路复用机制，比如说select，poll，epoll等。在Java NIO中同步事件分离器指的是Selector；对应的阻塞方法是select()方法；
3. Event Handler（事件处理器）：它本身由多个回调方法构成，这些回调方法构成了与应用相关的 对于某个事件的反馈机制。Netty相比于Java Nio来说，事件处理器角色上进行了一个升级，他给我们提供了大量的回调方法，供我们在特定事件产生时，实现相应的回调方法进行相应的业务处理。

![img](https:////upload-images.jianshu.io/upload_images/10707389-48ca4c5743f46fd6.png?imageMogr2/auto-orient/strip|imageView2/2/w/506/format/webp)



4. Concrete Event Handler（具体事件处理器）：它是事件处理器的实现。它本身实现了事件处理器所提供的各个回调方法，从而实现了特定的业务逻辑。他本质上就是我们所编写的一个个处理器的实现。
5. Initiation Dispatcher（初始分发器）：实际上Reactor角色。本身定义了一些规范，这些规范用语控制事件的调度方式，同时又提供了应用处理器的注册，删除等设施。它本身是事件的核心所在，Initiation Dispatcher会通过同步事件分离器来等待事件的发生 。一旦事件发生，Initiation Dispatcher首先会分离出每个事件，然后调用事件处理器，最后调用相关的回调方法来处理这些事件。

### Reactor模式的流程

Reactor模式的流程

1. 当应用向Initiation Dispatcher注册具体的事件处理器时，应用会标识出该事件处理器希望Initiation Dispatcher在某个事件发生时向其通知的该事件，该事件与 Handle 关联
2. Initiation Dispatcher会要求每个事件处理器向其传递内部的Handle。该Handle向操作系统标识了事件处理器。
3. 当所有的事件处理器注册完毕后，应用会调用handle_events方法来启动Initiation Dispatcher的事件循环。这时，Initiation Dispatcher会将每个注册的事件管理器的Handle合并起来，并使用同步事件分离器等待这些事件的发生。比如说，TCP协议层会使用select同步事件分离器操作来等待客户端发送的数据到达连接的socket handle上。
4. 当与某个事件源对应的Handle変为ready状态时（比如说，TCP socket変为等待读状态时），同步事件分离器就会通知Initiation Dispatcher。
5. Initiation Dispatcher会触发事件处理器的回调方法，从而响应这个处于ready状态的Handle。当事件发生时，Initiation Dispatcher会将被事件源激活的 Handle作为『key』来寻找并分发怡当的事件处理器回调方法。
6. Initiation Dispatcher会回调事件处理器的handle_events回调方法来执行特定于应用的功能（开发者自己所编写的功能），从而晌应这个事件。所发生的事件类型可以作为该方法参数并被该方法内部使用来执行额外的特定于服务的分离与分发。





#### 备注：

1. 一个EventLoopGroup当中会包含一个或多个EventLoop
2. 一个EventLoop在它的生命周期当中只会与唯一一个Thread进行绑定
3. 所有由EventLoop所处理的各种I/O事件都将在它所关联的哪个Thread上进行处理
4. 一个channel在它的整个生命周期中只会注册到一个EventLoop上
5. 一个EventLoop在运行过程中，会被分配一个或多个Channel
6. 所有提交到同一个channel的任务都会按照放入的顺序执行
7. 在netty中，channel的实现一定是线程安全的，基于此，我们可以存储一个channel的引用，并且在需要向远程端点发送数据时通过这个引用来调用channel相应的方法，即便当时有很多线程都在使用它，也不会出现多线程问题，而且消息一定会按照顺序发送。
8. 在通常的开发中不要将长时间执行的任务放入到EventLoop的执行队列中，因为它将会一直阻塞该线程所对应的所有channel上的其他执行任务。如果需要执行阻塞调用或者耗时操作，就需要使用一个专门的EventExecutor(业务线程池)
   - 在channelHandler的回调方法中，使用自己定义的业务线程池
   - 借助netty提供的向channelPipeline添加channelHandler时调用addLast方法添加EventExecutor

JDK所提供的Future只能通过手工方式检查执行结果，而这个操作当Future未执行完时会阻塞，netty对Future进行了增强，对isDone进行了扩展，增加了isSuccess，并增加了监听器的功能。同时ChannelFutureListener以回调的方式来获取结果，解决了手工检查的阻塞。值得注意的时ChannelFutureListener的operationComplete方法是由I/O线程执行的，因此要注意不要在次做耗时操作，看是否需要通过另外的线程池来执行。



```
public class DefaultChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ctx.writeAndFlush("");
        ctx.channel().writeAndFlush("");
    }
}
```

在netty中有两种发送消息的方式，可以直接写道channel中，也可以写道与channelHandler所关联的channelHandlerContext中，对于前一种方式来说，消息从channelPipeLine的末尾开始流动，对于后一种方式来说，消息从channelPipeLine中的下一个ChannelHandler开始流动。



结论：

1、ChannelHandlerContext与channelHandler之间的绑定关系是永远不会发生改变的，因此对其进行缓存时没有关系的

2、对于与channel同名的方法来说，channelHandlerContext的方法将会产生更短的事件，所以应该在可能的情况下利用这个特性来提升性能。



netty ByteBuffer所提供的三种缓冲类型：

1. heap buffer (byteArray在堆上创建，数据传输时会将堆内数据拷贝到堆外直接内存缓冲区，防止数据传输时发生GC，导致数据发生移动)
2. direct buffer (在虚拟机外创建)
3. composite buffer (复合缓冲区，可以同时储存heap buffer和direct buffer)

重点：对于后端的业务消息来说，推荐使用HeapByteBuf；对于I/O通信线程在读写缓冲区时，推荐使用DirectByteBuf

#### JDK的ByteBuffer和NettyByteBuf之间的区别

1. netty的ByteBuf采用的读写索引分离的策略（readIndex和writeIndex），一个初始化（里面尚未有任何数据）的ByteBuf中readIndex和writeIndex的值都为0
2. 当读索引和写索引相等时，不能就行读操作
3. 对于ByteBuf的任何读写操作都会分别单独维护读索引和写索引
4. netty存储字节是动态的

#### JDK ByteBuffer的缺点

JDK ByteBuffer底层用于存储堆内存，使用的是一个被final修饰的字节数组，一旦被分配好后，不能动态的扩容与收缩，当待存储的数据很大时，很可能出现下标越界异常，要解决这个问题，就需要知道待接收的数据大小。





# Java NIO中，关于DirectBuffer，HeapBuffer的疑问？

1. DirectBuffer 属于堆外存，那应该还是属于用户内存，而不是内核内存？
2. FileChannel 的read(ByteBuffer dst)函数,write(ByteBuffer src)函数中，如果传入的参数是HeapBuffer类型,则会临时申请一块DirectBuffer,进行数据拷贝，而不是直接进行数据传输，这是出于什么原因？



Java NIO中的direct buffer（主要是DirectByteBuffer）其实是分两部分的：

```text
       Java        |      native
                   |
 DirectByteBuffer  |     malloc'd
 [    address   ] -+-> [   data    ]
                   |
```

其中 DirectByteBuffer 自身是一个Java对象，在Java堆中；而这个对象中有个long类型字段address，记录着一块调用 malloc() 申请到的native memory。



所以回到题主的问题：

> 1. DirectBuffer 属于堆外存，那应该还是属于用户内存，而不是内核内存？

DirectByteBuffer 自身是（Java）堆内的，它背后真正承载数据的buffer是在（Java）堆外——native memory中的。这是 malloc() 分配出来的内存，是用户态的。



> 2. FileChannel 的read(ByteBuffer dst)函数,write(ByteBuffer src)函数中，如果传入的参数是HeapBuffer类型,则会临时申请一块DirectBuffer,进行数据拷贝，而不是直接进行数据传输，这是出于什么原因？

题主看的是OpenJDK的 sun.nio.ch.IOUtil.write(FileDescriptor fd, ByteBuffer src, long position, NativeDispatcher nd) 的实现对不对：

```java
    static int write(FileDescriptor fd, ByteBuffer src, long position,
                     NativeDispatcher nd)
        throws IOException
    {
        if (src instanceof DirectBuffer)
            return writeFromNativeBuffer(fd, src, position, nd);

        // Substitute a native buffer
        int pos = src.position();
        int lim = src.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        ByteBuffer bb = Util.getTemporaryDirectBuffer(rem);
        try {
            bb.put(src);
            bb.flip();
            // Do not update src until we see how many bytes were written
            src.position(pos);

            int n = writeFromNativeBuffer(fd, bb, position, nd);
            if (n > 0) {
                // now update src
                src.position(pos + n);
            }
            return n;
        } finally {
            Util.offerFirstTemporaryDirectBuffer(bb);
        }
    }
```

这里其实是在迁就OpenJDK里的HotSpot VM的一点实现细节。

HotSpot VM里的GC除了CMS之外都是要移动对象的，是所谓“compacting GC”。

如果要把一个Java里的 byte[] 对象的引用传给native代码，让native代码直接访问数组的内容的话，就必须要保证native代码在访问的时候这个 byte[] 对象不能被移动，也就是要被“pin”（钉）住。

可惜HotSpot VM出于一些取舍而决定不实现单个对象层面的object pinning，要pin的话就得暂时禁用GC——也就等于把整个Java堆都给pin住。HotSpot VM对JNI的Critical系API就是这样实现的。这用起来就不那么顺手。

所以 Oracle/Sun JDK / OpenJDK 的这个地方就用了点绕弯的做法。它假设把 HeapByteBuffer 背后的 byte[] 里的内容拷贝一次是一个时间开销可以接受的操作，同时假设真正的I/O可能是一个很慢的操作。

于是它就先把 HeapByteBuffer 背后的 byte[] 的内容拷贝到一个 DirectByteBuffer 背后的native memory去，这个拷贝会涉及 sun.misc.Unsafe.copyMemory() 的调用，背后是类似 memcpy() 的实现。这个操作本质上是会在整个拷贝过程中暂时不允许发生GC的，虽然实现方式跟JNI的Critical系API不太一样。（具体来说是 Unsafe.copyMemory() 是HotSpot VM的一个intrinsic方法，中间没有safepoint所以GC无法发生）。

然后数据被拷贝到native memory之后就好办了，就去做真正的I/O，把 DirectByteBuffer 背后的native memory地址传给真正做I/O的函数。这边就不需要再去访问Java对象去读写要做I/O的数据了。



#### 自旋锁

AtomicIntegerFieldUpdater要点：	

1. ​	更新器更新的必须是int类型变量。不能是其包装类型
2. 更新器更新的必须是volatile类型变量，确保线程之间变量的立即可见性
3. 变量不能是static的，必须是实例变量，因为Unsafe.objectFieldOffset()方法不支持静态变量（CAS操作本质上是通过对象实例的偏移来进行取值的）
4. 更新器只能修改它可见范围变量，因为更新器是根据反射来获取到这个变量的。