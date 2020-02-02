package jdk.NIO.socket.chat;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * 服务端连接事件
 */
public interface ServerListener {

    /**
     * 服务器启动成功事件
     *
     * @param chatServer
     */
    void serverStart(ChatServer chatServer);

    /**
     * 连接关闭
     *
     * @see SelectionKey#isValid()
     * @param chatServer
     * @param selectionKey
     */
    void notValid(ChatServer chatServer, SelectionKey selectionKey);

    /**
     * 服务端空闲事件，无客户端事件需要处理
     * @param chatServer
     */
    void idle(ChatServer chatServer);

    /**
     * 客户端连接事件
     * @param chatServer
     */
    void accept(ChatServer chatServer) throws IOException;

    /**
     * 有可读数据
     *
     * @param chatServer
     * @param selectionKey
     */
    void readable(ChatServer chatServer, SelectionKey selectionKey) throws IOException;
}
