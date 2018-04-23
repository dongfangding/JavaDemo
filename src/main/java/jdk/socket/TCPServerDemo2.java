package jdk.socket;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerDemo2 {

    @Test
    public void simpleSendReceServer() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            // 创建socket服务端，监视10003端口
            serverSocket = new ServerSocket(10003);
            System.out.println("Server Start.....");
            clientSocket = serverSocket.accept(); // accept方法会阻塞程序，等待客户端来连接
            // 获得客户端的输入流，读取客户端发送的数据
            InputStream input = clientSocket.getInputStream();
            byte[] buf = new byte[1024];
            int len = input.read(buf);
            String str = new String(buf, 0, len);
            String ip = clientSocket.getInetAddress().getHostAddress();
            System.out.println("recever data from client<" + ip + "> content:" + str);
            // 获得客户端的输出流，向客户端返送数据
            OutputStream out = clientSocket.getOutputStream();
            out.write("copy that!".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将客户端发送的字符转换为大写并返回
     */
    @Test
    public void textTransServer() {
        ServerSocket serverSocket = null;
        Socket client = null;
        try {
            // 创建服务端对象，并监视一个端口
            serverSocket = new ServerSocket(10004);
            System.out.println("Server Start....");
            while (true) {
                client = serverSocket.accept();
                BufferedReader brIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter brOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                String line = null;
                while ((line = brIn.readLine()) != null) {
                    System.out.println("Server recever: " + line);
					/*brOut.write(line.toUpperCase());
					brOut.newLine();
					brOut.flush();*/
                    pw.println(line.toUpperCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
