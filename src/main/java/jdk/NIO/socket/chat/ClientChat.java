package jdk.NIO.socket.chat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientChat {

    private SocketChannel socketChannel;

    private String host;

    private Integer port;

    public ClientChat(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void connnect() throws Exception{
        // 创建客户端Socket
        SocketChannel socketChannel = SocketChannel.open();
        // 设置为非阻塞模式
        socketChannel.configureBlocking(false);
        // 连接服务器
        boolean connect = socketChannel.connect(new InetSocketAddress(host, port));
        // 要先保证客户端能正确连接到服务器
        if (!connect) {
            while (!socketChannel.finishConnect()) {
                System.out.println("连接重试中。。。。。。。。。。。。。");
            }
        }
        System.out.println("客户端已启动。。。。。。。。。。。");
        // 启动一个线程去轮询接收数据
        new Thread(() -> {
            while (true) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                try {
                    int count = socketChannel.read(byteBuffer);
                    if (count > 0) {
                        System.out.println(new String(byteBuffer.array()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
            socketChannel.write(byteBuffer);
        }

    }

    public static void main(String[] args) throws Exception {
        ClientChat clientChat = new ClientChat("127.0.0.1", 9999);
        clientChat.connnect();
    }
}
