package jdk.NIO.socket.demo;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * 服务端代码
 *
 */
public class ServerSocketChannelDemo {

    public static void main(String[] args) throws Exception {
        // 创建ServerSocketChannel对象
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 创建选择器
        Selector selector = Selector.open();

        // 服务器暴露本地端口进行监听
        serverSocketChannel.bind(new InetSocketAddress(8888));

        // 将服务器Socket对象注册到选择器上
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 开始做事

        while (true) {
            // 说明当前没有要处理的事件
            if (selector.select(2000) == 0) {
                continue;
            }

            // 获取可用的事件列表
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey eventKey = iterator.next();

                // 是否已准备连接
                if (eventKey.isAcceptable()) {
                    // 接受一个客户端连接
                    SelectableChannel channel = serverSocketChannel.accept();
                    // 设置为非阻塞模式
                    channel.configureBlocking(false);
                    // 注册客户端的读写事件
                    channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }

                // 读事件就绪
                if (eventKey.isReadable()) {
                    SelectableChannel channel = eventKey.channel();
                    if (channel instanceof SocketChannel) {
                        SocketChannel socketChannel = (SocketChannel) channel;
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int read = socketChannel.read(byteBuffer);
                        if (read > 0) {
                            System.out.printf(String.format("读取到客户端%s发送的数据: %s \n",
                                    socketChannel.getRemoteAddress().toString(), new String(byteBuffer.array())));
                        }
                    }
                }

                // 移除当前事件，避免重复处理
                iterator.remove();
            }
        }
    }
}
