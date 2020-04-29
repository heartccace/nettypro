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

### 传统阻塞BIO模型

​		传统的网络编程IO模型采用的时阻塞IO，通常的处理方式是一个客户端在服务器端对应一个线程。

当客户端数量增多时，线程数量增多。当线程到达一定数量后会导致系统性能下降。或许可以采用线程池来处理，但是请求数量多的情况下依然无法有较好的性能。

​		传统网络编程的阻塞点在ServerSocket的accept方法和InputStream的read方法，如果一直未接收到来自客户端的连接，线程资源就会一直被浪费。

​	BIO是面向流进行编程的

### NIO非阻塞模型

​		NIO（Non-Block IO）非阻塞编程，面向块编程。采用selector、buffer、channel进行数据传输。一个selector可以注册多个channel，channel直接操作buffer对数据进行读写。

NIO采用事件驱动模型。NIO通过selector进行事件注册，由selector轮询注册事件，当事件发生直接交由线程处理。一个线程对应一个selector，一个selector可以对应多个事件，一个selector可以处理多个channel，每个channel对应一个buffer。

NIO基于IO多路复用模型：多个连接复用一个阻塞对象

## Reactor模型

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