package jdk.NIO.socket.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.List;
import java.util.Set;


/**
 * 默认事件实现
 */
public class ServerListenerImpl implements ServerListener {
    /**
     * 服务器启动成功事件
     *
     * @param chatServer
     */
    @Override
    public void serverStart(ChatServer chatServer) {
        System.out.println("服务器已启动成功!!");
    }

    /**
     * 连接关闭
     *
     * @param chatServer
     * @param selectionKey
     * @see SelectionKey#isValid()
     */
    @Override
    public void notValid(ChatServer chatServer, SelectionKey selectionKey) {
        SelectableChannel channel = selectionKey.channel();
        if (channel instanceof SocketChannel) {
            chatServer.removeSocketChannel(((SocketChannel) channel));
        }
    }


    /**
     * 服务端空闲事件，无客户端事件需要处理
     * @param chatServer
     */
    @Override
    public void idle(ChatServer chatServer) {
        System.out.println("当前无客户端事件， 服务器处于空闲状态！");
    }

    /**
     * 客户端连接事件
     *
     * @param chatServer
     */
    @Override
    public void accept(ChatServer chatServer) throws IOException {
        // 接受一个客户端连接
        SelectableChannel channel = chatServer.getServerSocketChannel().accept();
        if (channel != null) {
            // 设置为非阻塞模式
            channel.configureBlocking(false);
            // 注册客户端的读写事件
            channel.register(chatServer.getSelector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    /**
     * 有可读数据
     *
     * @param chatServer
     * @param selectionKey
     */
    @Override
    public void readable(ChatServer chatServer, SelectionKey selectionKey) throws IOException {
        SelectableChannel channel = selectionKey.channel();
        if (channel instanceof SocketChannel) {
            SocketChannel socketChannel = (SocketChannel) channel;
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            // 有数据可读的情况下才处理
            if (socketChannel.read(byteBuffer) > 0) {
                // 服务端先打印收到的数据
                System.out.printf(String.format("接受到%s发送的数据: %s\n", socketChannel.getRemoteAddress().toString()
                        .substring(1), new String(byteBuffer.array())));


                List<SocketChannel> socketChannelList = chatServer.getSocketChannelList();
                if (socketChannelList != null && !socketChannelList.isEmpty()) {
                    ByteBuffer wrap = ByteBuffer.wrap(socketChannel.getRemoteAddress().toString().substring(1)
                            .concat("说: ").concat(new String(byteBuffer.array())).getBytes());
                    for (SocketChannel socketChannel1 : socketChannelList) {
                        if (socketChannel == socketChannel1) {
                            continue;
                        }
                        socketChannel1.write(wrap);
                    }
                }

                // 将数据广播出去，但排除当前客户端
                /*Set<SelectionKey> selectionKeys = chatServer.getSelector().selectedKeys();
                if (selectionKeys.size() > 0) {
                    for (SelectionKey key : selectionKeys) {
                        SelectableChannel destChannel = key.channel();
                        if (channel == destChannel) {
                            // 排除当前客户端
                            continue;
                        }

                        // 如果不是则往该通道写入读取到的数据
                        if (destChannel instanceof SocketChannel) {
                            SocketChannel destSocketChannel = (SocketChannel) destChannel;
                            ByteBuffer wrap = ByteBuffer.wrap(socketChannel.getRemoteAddress().toString().substring(1)
                                    .concat("说: ").concat(new String(byteBuffer.array())).getBytes());
                            destSocketChannel.write(wrap);
                        }
                    }
                }*/
            }
        }
    }
}
