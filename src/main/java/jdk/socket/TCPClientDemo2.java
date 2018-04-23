package jdk.socket;

import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClientDemo2 {


    /**
     * 简单的一个服务端与客户端连接的小程序
     */
    @Test
    public void simpleSendReceClient() {
        Socket socket = null;
        try {
            // 创建客户端socket连接对象，需要明确要链接的主机和端口号
            socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 10003);
            // 获得输出流向服务端写数据
            OutputStream out = socket.getOutputStream();
            out.write("TCP Demo: Server, do you copy ?".getBytes());
            // 获得输入流读取服务端返回的是数据
            InputStream input = socket.getInputStream();
            byte[] buf = new byte[1024];
            int len = input.read(buf);
            String receStr = new String(buf, 0, len);
            System.out.println("recever data from server：" + receStr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向文本转换服务发送需要转换为大写的字符
     */
    @Test
    public void textTransClient() {
        Socket socket = null;
        BufferedReader brRead = null;
        try {
            // 创建客户端socket，并连接服务端主机和端口
            socket = new Socket(InetAddress.getLocalHost().getHostName(), 10004);
            // 获得向服务端写数据的输出流
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            // 读取键盘录入
            brRead = new BufferedReader(new InputStreamReader(System.in));
            // 获得向服务端写数据的输出流
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // 获得接收服务端返回数据的输入流
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = "";
            // readLine是阻塞式读取。必须明确结束符才能确保退出循环
            while ((line = brRead.readLine()) != null) {
                if ("exit".equals(line)) {
                    break;
                }
                System.out.println("write: " + line);
                // 要么使用PrintWriter的true参数自动刷新，并且调用println()方法打印换行符来让服务端明确什么时候该读取结束，
                // pw.println(line);
                // 要么使用缓冲流的write方法，并且在每次结束的时候调用newLine方法写一个换行符，并且调用刷新方法，将数据刷出
                bw.write(line);
                bw.newLine();
                bw.flush();
                String receStr = brIn.readLine();
                System.out.println("copy: " + receStr);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (brRead != null) {
                try {
                    brRead.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
