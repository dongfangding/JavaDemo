package main.java.jdk.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPUploadServer {
	public static void main(String []args) {
		// uploadTextClient();
		uploadPictureServer();
	}

	/**
	 * 上传文本的服务端
	 */
	private static void uploadTextClient() {
		ServerSocket serverSocket = null;
		Socket client = null;
		BufferedWriter bw = null;
		try {
			// 创建服务端，监视一个端口
			serverSocket = new ServerSocket(10005);
			System.out.println("Server Start....");
			while(true) {
				// 获得客户端
				client = serverSocket.accept();
				System.out.println(client.getInetAddress().getHostAddress() + "connected...");
				// 获得客户端对象的输出流，向客户端发送上传结果
				BufferedWriter bwOut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				// 获得客户端对象的输入流，用来读取客户端发送的数据
				BufferedReader brIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
				File file = new File("clientText-Server.txt");
				bw = new BufferedWriter(new FileWriter(file));
				String line = null;
				while((line = brIn.readLine()) != null) {
					bw.write(line);
					bw.newLine();
					bw.flush();
					System.out.println("服务端写入文件数据： " + line);
				}
				System.out.println("上传路径： " + file.getAbsolutePath());
				bwOut.write("上传成功");
				bwOut.newLine();
				bwOut.flush();
				// 给客户端的输出流加入结束标记
				client.shutdownOutput();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 上传图片的客户端
	 */
	private static void uploadPictureServer() {
		ServerSocket server = null;
		Socket socket = null;
		try {
			// 创建服务端，监视一个端口
			server = new ServerSocket(10005);
			System.out.println("Server Start....");
			// 获得客户端
			socket = server.accept();
			System.out.println(socket.getInetAddress().getHostAddress() + "connected...");
			// 获得客户端对象的输入流，用来读取客户端发送的图片数据
			InputStream socketInput = socket.getInputStream();
			// 获得客户端对象的输出流，向客户端发送上传结果
			OutputStream socketOutput = socket.getOutputStream();
			// 创建写入的文件名称
			File file = new File("1-server.png");
			FileOutputStream fos = new FileOutputStream(file);
			byte []buf = new byte[1024];
			int picLen = 0;
			while((picLen = socketInput.read(buf)) != -1) {
				fos.write(buf, 0, picLen);
				fos.flush();
			}
			System.out.println("上传路径： " + file.getAbsolutePath());
			// 向客户端返回上传结果
			socketOutput.write("上传图片成功".getBytes());
			socketOutput.flush();
			// 给客户端的输出流加入结束标记
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
