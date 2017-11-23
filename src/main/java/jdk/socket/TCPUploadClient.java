package main.java.jdk.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * 向服务器上传文件
 * 一般出现客户端 和服务器都没有反应的时候，很有可能就是服务端和客户端的流都在互相等待对方的数据，所以需要检查代码中
 * 有哪些代码会引起输入流会一直处于阻塞状态
 * @author Administrator
 *
 */
public class TCPUploadClient {
	public static void main(String[] args) {
		// uploadTextClient();
		uploadPictureClient();
	}
	
	/**
	 * 上传图片的客户端
	 */
	private static void uploadTextClient() {
		Socket socket = null;
		BufferedReader br = null;
		try {
			// 创建客户端，并指定连接的主机和端口
			socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 10005);
			// 获得本地需要上传的文本读取流
			br = new BufferedReader(new FileReader("clientText.txt"));
			// 获得socket的输出流，向服务端写入数据
			BufferedWriter bwOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			// 获得socket的输入流，读取服务端返回的数据
			BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;
			while((line = br.readLine()) != null) {
				// 本地文件客户端可以能够独到文本的结束符
				bwOut.write(line);
				bwOut.newLine();
				bwOut.flush();
				System.out.println("客户端写入数据： " + line);
			}
			// 本地读完之后要告诉服务器，给输出流加结束标记，避免服务器的输入流一直阻塞等待读取数据
			socket.shutdownOutput();
			String receStr = null;
			while((receStr = brIn.readLine()) != null) {
				System.out.println("上传文件的结果: " + receStr);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 上传图片客户端
	 */
	private static void uploadPictureClient() {
		Socket socket = null;
		FileInputStream fis = null;
		try {
			// 创建客户端socket流。连接目标主机和端口
			socket = new Socket(InetAddress.getLocalHost().getHostAddress(), 10005);
			// 获得需要上传的图片的字节流对象
			fis = new FileInputStream("1.png");
			// 获得socket输出流，向服务端写入图片数据
			OutputStream socketOutput = socket.getOutputStream();
			InputStream socketInput = socket.getInputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = fis.read(buf)) != -1) {
				socketOutput.write(buf, 0, len);
				socketOutput.flush();
			} 
			// 给当前的输出流写入结束标记，服务端的输入流才能正确的知道何时读取结束
			socket.shutdownOutput();
			byte[] bufIn = new byte[1024];
			int lenIn = 0;
			String str = "";
			while((lenIn = socketInput.read(bufIn)) != -1) {
				str += new String(bufIn, 0, lenIn);
			}
			System.out.println("服务端返回的数据为：" + str);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
