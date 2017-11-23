package main.java.jdk.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPSendDemo1 {

	public static void main(String[] args) {
		// simpleUdpSendClient();
		updSendClient2();
	}

	private static void simpleUdpSendClient() {
		// 创建UDP传输对象
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			// 创建传输字符
			String message = "udp传输演示2323232";
			// 创建数据包使用DatagramPacket,数据报包用来实现无连接包投递服务,从一台机器发送到另
			// 一台机器的多个包可能选择不同的路由，也可能按不同的顺序到达,不对包投递做出保证。 
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
					InetAddress.getLocalHost(), 10000);
			// 发送数据包
			socket.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭发送流
			if(socket != null) {
				socket.close();
			}
		}
	}
	
	private static void updSendClient2() {
		DatagramSocket sendSocket = null;
		try {
			// 创建UDP服务
			sendSocket = new DatagramSocket();
			// 读取键盘录入
			BufferedReader bfR = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			while((line = bfR.readLine()) != null) {
				System.out.println("输入数据： " + line);
				DatagramPacket sendPacket = new DatagramPacket(line.getBytes()
						, line.getBytes().length, InetAddress.getLocalHost(), 10001);
				sendSocket.send(sendPacket);
				if("886".equals(line.trim())) {
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*if(sendSocket != null) {
				sendSocket.close();
			}*/
		}
	}

}
