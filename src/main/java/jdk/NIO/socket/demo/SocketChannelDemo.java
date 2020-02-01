package jdk.NIO.socket.demo;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * 客户端代码
 */
public class SocketChannelDemo {

    public static void main(String[] args) throws Exception {

        // 创建客户端Socket对象
        SocketChannel socketChannel = SocketChannel.open();

        // 设置非阻塞模式
        socketChannel.configureBlocking(false);

        // 连接服务器
        boolean connectResult = socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888));

        // 要先保证客户端能正确连接到服务器
        if (!connectResult) {
            while (!socketChannel.finishConnect()) {
                System.out.println("连接重试中。。。。。。。。。。。。。");
            }
        }

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();

            ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());

            // 向服务器发送数据
            socketChannel.write(byteBuffer);
        }


    }
}
