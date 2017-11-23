package main.java.jdk.socket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * 上传图片的服务端的多线程处理
 * @author DingDongfang
 *
 */
public class TCPUploadServerThread {
	public static void main(String[] args) {
		ServerSocket server = null;
		try {
			// 启动服务端
			server = new ServerSocket(10005);
			System.out.println("当前线程：" + Thread.currentThread().getName());
			System.out.println("Server Start...");
			while(true) {
				// 获取一个客户端，启动一个线程去处理上传图片的过程
				Socket socket = server.accept();
				System.out.println(socket.getInetAddress().getHostAddress() + " connected...");
				new Thread(new UploadTask(socket)).start();;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class UploadTask implements Runnable {
	private Socket socket;
	public UploadTask(Socket socket) {
		this.socket = socket;
	}
	@Override
	public void run() {
		FileOutputStream fos = null;
		try {
			System.out.println("处理上传图片的线程为:" + Thread.currentThread().getName());
			// 获得客户端对象的输入流，用来读取客户端发送的图片数据
			InputStream socketInput = socket.getInputStream();
			// 获得客户端对象的输出流，向客户端发送上传结果
			OutputStream socketOutput = socket.getOutputStream();
			// 创建写入的文件名称(本地流需要单独关闭)
			File file = new File("1-server"+new Date().getTime()+".png");
			fos = new FileOutputStream(file);
			byte []buf = new byte[1024];
			int picLen = 0;
			while((picLen = socketInput.read(buf)) != -1) {
				fos.write(buf,0,picLen);
				fos.flush();
			}
			System.out.println("上传路径： " + file.getAbsolutePath());
			// 向客户端返回上传结果
			socketOutput.write("upload success!".getBytes());
			socketOutput.flush();
			// 给客户端的输出流加入结束标记
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 本地流需要单独关闭
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// socket关闭，则socket的流对象也自动关闭
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
