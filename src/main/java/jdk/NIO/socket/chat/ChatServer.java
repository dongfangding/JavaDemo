package jdk.NIO.socket.chat;


import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

/**
 * 聊天服务器服务端
 * fixme 如何在创建连接的时候传递一些认证参数，之后每个连接都能找到自己的认证身份
 */
public class ChatServer {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private Integer port;

    private List<SocketChannel> socketChannelList;

    /**
     * 监听事件
     */
    private ServerListener serverListener;

    public ChatServer(Integer port, ServerListener serverListener) {
        this.port = port;
        this.serverListener = serverListener;
    }

    public void start() throws Exception{
        // 创建服务端Socket
        serverSocketChannel = ServerSocketChannel.open();
        // 配置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 监听本地端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        // 创建选择器
        selector = Selector.open();
        // 将服务端Socket注册到选择器中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        serverListener.serverStart(this);

        while (true) {
            // 判断是否有就绪事件
            if (selector.select(5000) == 0) {
                serverListener.idle(this);
                continue;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey nextKey = iterator.next();

                // 客户端连接请求
                if (nextKey.isAcceptable()) {
                    serverListener.accept(this);
                }

                // 客户端有可读的数据
                if (nextKey.isReadable()) {
                    serverListener.readable(this, nextKey);
                }

                if (!nextKey.isValid()) {

                }

                // 移除当前已处理的事件
                iterator.remove();
            }
        }
    }

    public void addSocketChannel(SocketChannel socketChannel) {
        socketChannelList.add(socketChannel);
    }

    public void removeSocketChannel(SocketChannel socketChannel) {
        socketChannelList.remove(socketChannel);
    }

    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public void setServerSocketChannel(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<SocketChannel> getSocketChannelList() {
        return socketChannelList;
    }

    public void setSocketChannelList(List<SocketChannel> socketChannelList) {
        this.socketChannelList = socketChannelList;
    }

    public ServerListener getServerListener() {
        return serverListener;
    }

    public void setServerListener(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    /**
     * 启动服务器
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ServerListener serverListener = new ServerListenerImpl();
        ChatServer chatServer = new ChatServer(9999, serverListener);
        chatServer.start();
    }

}
