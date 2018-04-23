package jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 局域网内聊天小程序。
 * 利用多线程，同时收发消息
 * ip地址的最后一段255代表广播地址，发送的数据会被广播到同网段的所有机器上。
 *
 * @author DingDongfang on 2017年1月5日 上午10:52:50
 */
public class UDPChat {
    public static void main(String[] args) {
        try {
            // 发送线程
            DatagramSocket sendSocket = new DatagramSocket();
            // 255是广播网段， 发送的数据包在同一网段的都能收到
            // 192.168.1.255
            UDPChatSend udpChatSend = new UDPChatSend(sendSocket,
                    InetAddress.getLocalHost(), 10002);
            new Thread(udpChatSend).start();

            // 接收线程
            DatagramSocket receSocket = new DatagramSocket(10002);
            UDPChatRece udpChatRece = new UDPChatRece(receSocket);
            new Thread(udpChatRece).start();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

class UDPChatSend implements Runnable {
    private DatagramSocket sendDs;
    private InetAddress address;
    private int port;

    UDPChatSend(DatagramSocket sendDs, InetAddress address, int port) {
        this.sendDs = sendDs;
        this.address = address;
        this.port = port;
        System.out.println("发送端启动.....");
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            StringBuilder sbl = new StringBuilder();
            while ((line = br.readLine()) != null) {
                byte[] buf = line.getBytes();
                DatagramPacket dp = new DatagramPacket(buf, buf.length, address, port);
                sendDs.send(dp);
                if ("886".equals(sbl.toString())) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sendDs != null) {
                sendDs.close();
            }
        }
    }
}

class UDPChatRece implements Runnable {
    private DatagramSocket receDs;
    private boolean isExit = false;

    UDPChatRece(DatagramSocket receDs) {
        this.receDs = receDs;
        System.out.println("接收端启动.....");
    }

    public void exit() {
        this.isExit = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                receDs.receive(dp);
                if (dp != null && dp.getData().length > 0) {
                    String receData = new String(dp.getData(), 0, dp.getData().length);
                    String ip = dp.getAddress().getHostAddress();
                    System.out.println(ip + ": " + receData);
                    if ("886".equals(receData.trim())) {
                        System.out.println(ip + "退出聊天室...");
                    }
                    if ("exit".equals(receData.trim())) {
                        this.exit();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (isExit) {
                    if (receDs != null) {
                        receDs.close();
                    }
                    System.out.println("关闭聊天室");
                    break;
                }
            }
        }
    }
}
