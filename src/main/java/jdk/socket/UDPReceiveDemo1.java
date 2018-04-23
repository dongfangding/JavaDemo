package jdk.socket;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPReceiveDemo1 {


    /**
     * UDP协议的发送，需要配合UDPSendDemo1来使用，先开启接收端，再开启发送端
     */
    @Test
    public void simpleUdpReceServer() {
        DatagramSocket receSocket = null;
        try {
            // 创建UDP socket服务,因为是接收端，必须明确一个端口号
            receSocket = new DatagramSocket(10000);
            System.out.println("接收端启动.......");
            // 不知道如何动态定义接收字节数组大小
            byte[] buf = new byte[1024];
            // 创建接收包
            DatagramPacket recePacket = new DatagramPacket(buf, buf.length);
            receSocket.receive(recePacket);
            if (recePacket != null && recePacket.getData().length > 0) {
                String ip = recePacket.getAddress().getHostAddress();
                // 获得数据的缓冲区
                byte[] data = recePacket.getData();
                String dataStr = new String(data, 0, data.length);
                System.out.println("接收到<" + ip + ">:  " + dataStr);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 根据实际情况是否关闭接收流
			/*if(receSocket != null) {
				receSocket.close();
			}*/
        }
    }


    @Test
    public void udpReceServer2() {
        DatagramSocket receSocket = null;
        try {
            // 创建UDP socket服务,因为是接收端，必须明确一个端口号
            receSocket = new DatagramSocket(10001);
            System.out.println("接收端启动.......");
            while (true) {
                // 不知道如何动态定义接收字节数组大小
                byte[] buf = new byte[1024];
                // 创建接收包
                DatagramPacket recePacket = new DatagramPacket(buf, buf.length);
                receSocket.receive(recePacket);
                if (recePacket != null && recePacket.getData().length > 0) {
                    // 获得数据的缓冲区
                    byte[] data = recePacket.getData();
                    String dataStr = new String(data, 0, data.length);
                    String ip = recePacket.getAddress().getHostAddress();
                    if ("886".equals(dataStr.trim())) {
                        System.out.println(ip + "退出聊天室！");
                    } else {
                        System.out.println("接收到<" + ip + ">:  " + dataStr);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (receSocket != null) {
                receSocket.close();
            }
        }
    }

}
