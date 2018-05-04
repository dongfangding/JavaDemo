package jdk.socket;

import org.junit.jupiter.api.Test;;

import java.io.*;
import java.net.*;

/**
 * @author Administrator
 */
public class TCPMyTomcatBrowser {

    // myTomcat(); //socket模拟tomcat响应数据,无交互，单独运行即可，只是返回固定的数据
    // myTomcat2(); // 和myBrowser4一起运行， 一个简单的服务端，只负责将客户端的数据打印出来
    // myBrowser(); // socket模拟浏览器发送请求,需要启动tomcat
    // myBrowser2(); // URL对象访问资源,需要启动tomcat
    // myBrowser3(); // URLConnection对象，需要启动tomcat
    // myBrowser4(); // 和myTomcat2一起运行
    // myBrowser5();

    /**
     * 浏览器是一个客户端，模拟浏览器访问http://localhost:10007,给浏览器返回数据
     * 返回一个简单的表格
     * 返回一张图片
     * 两个只能同时返回一个，否则会破坏图片的字节流，导致图片出现问题
     */
    @Test
    public void myTomcat() {
        ServerSocket server = null;
        Socket client = null;
        try {
            server = new ServerSocket(10007);
            System.out.println("Server start....");
            client = server.accept();
            System.out.println(client.getInetAddress().getHostAddress() + "connected.");
            InputStream input = client.getInputStream();
            byte[] buf = new byte[1024];
            int len = input.read(buf);
            String request = new String(buf, 0, len);
            System.out.println("浏览器的请求参数：");
            System.out.println(request);
            // 向浏览器返回一个简单表格
			/*PrintWriter pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
			pw.println("<table border = 1><th>new a table</th><tr><td>name</td><td>dongfang.ding</td></tr>"
					+ "<tr><td>sex</td><td>man</td></tr></table>");*/
            OutputStream out = client.getOutputStream();
            // 向浏览器返回一张图片
            byte[] bufPic = new byte[1024];
            FileInputStream fis = new FileInputStream("1.png");
            int picLen = 0;
            while ((picLen = fis.read(bufPic)) != -1) {
                out.write(bufPic, 0, picLen);
                out.flush();
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

    /**
     * 一个简单的服务器，只负责打印客户端发送的数据
     */
    @Test
    public void myTomcat2() {
        ServerSocket server = null;
        Socket client = null;
        try {
            server = new ServerSocket(8083);
            System.out.println("Server Start.....");
            client = server.accept();
            InputStream inClient = client.getInputStream();
            BufferedReader bw = new BufferedReader(new InputStreamReader(inClient));
            String line = null;
            while ((line = bw.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟一个浏览器， 向服务端请求一个资源
     * socket返回的信息包含http响应头信息，该部分真正的浏览器会去解析，
     * 在此例中，会返回头信息。
     */
    @Test
    public void myBrowser() {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 8082);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            // 请求方式 请求资源 http协议版本
            pw.println("POST /examples/index.html HTTP/1.1");
            pw.print("Accept: */*");
            pw.println("Accept-Language: zh-CN,zh;q=0.8");
            pw.println("Host: localhost:8082");
            pw.println("Connection: keep-alive");
            // 不管是否存在请求体，请求头和请求体之间必须要有一个空格,
            pw.println();
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String brInLine = null;
            while ((brInLine = brIn.readLine()) != null) {
                System.out.println(brInLine);
            }
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
     * 使用java.net.URL对象连接服务器资源，此对象内部封装了socket操作，
     * 此对象可以获得一个主机连接信息对象URlConnection对象，可以操作、解析http响应信息
     */
    @Test
    public void myBrowser2() {
        InputStream in = null;
        try {
            URL url = new URL("http://localhost:8082/examples/index.html");
            in = url.openStream();
            // url.openStream内部使用的是下面的两句话，依然还是URLConnection对象
			/*URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();*/
            BufferedReader bw = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = bw.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 详解URLConnection对象
     * 通常，创建一个到 URL 的连接需要几个步骤：
     * 1. 通过在 URL 上调用 openConnection 方法创建连接对象。
     * 2. 处理设置参数和一般请求属性。
     * 3. 使用 connect 方法建立到远程对象的实际连接。
     * 4. 远程对象变为可用。远程对象的头字段和内容变为可访问。
     * 使用以下方法修改设置参数：
     * setAllowUserInteraction 如果为 true，则在允许用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查。
     * setDoInput 将 doInput 标志设置为 true，指示应用程序要从 URL 连接读取数据,默认true
     * setDoOutput 将 doOutput 标志设置为 true，指示应用程序要将数据写入 URL 连接。 此字段的默认值为 false。
     * setIfModifiedSince 默认一直获取协议对象，不允许跳过
     * setUseCaches 如果为 true，则只要有条件就允许协议使用缓存。如果为 false，则该协议始终必须获得此对象的新副本。
     */
    @Test
    public void myBrowser3() {
        InputStream in = null;
        try {
            // 创建统一资源定位符对象，可以是任何网络资源
            URL url = new URL("http://localhost:8082/examples/index.html");
            // 获得HttpURLConnection连接对象
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            // 实际使用为false，也没啥影响,具体没懂
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            // application/x-www-form-urlencoded封装请求参数，如果是get方法则将参数拼接到url后面，
            // 如果是post方法，则放在请求体中,默认纯文本，还有一个是文件的
            httpConn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Accept-Charset", "utf-8");
            httpConn.connect();
            BufferedReader bw = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line = null;
            while ((line = bw.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自定义浏览器和服务器发送数据，测试outputstream。未成功。
     */
    @Test
    public void myBrowser4() {
        InputStream in = null;
        try {
            // 创建统一资源定位符对象，可以是任何网络资源
            URL url = new URL("http://localhost:8083");
            // 获得HttpURLConnection连接对象
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            // 实际使用为false，也没啥影响,具体没懂
            httpConn.setDoOutput(true);
            OutputStreamWriter ow = new OutputStreamWriter(httpConn.getOutputStream());
            ow.write("测试DoOutput的效果");
            ow.flush();
            httpConn.connect();
            BufferedReader bw = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line = null;
            while ((line = bw.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 模拟真实的向服务端请求数据,未成功！
     */
    @Test
    public void myBrowser5() {
        try {
            // 创建统一资源定位符对象，可以是任何网络资源
            URL url = new URL("http://120.25.126.214:8080/HiDesk/front");//
            // 获得HttpURLConnection连接对象
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            // 实际使用为false，也没啥影响,具体没懂
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            // application/x-www-form-urlencoded封装请求参数，如果是get方法则将参数拼接到url后面，
            // 如果是post方法，则放在请求体中,默认纯文本，还有一个是文件的
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Accept-Charset", "utf-8");
            OutputStreamWriter ow = new OutputStreamWriter(httpConn.getOutputStream());
            //ow.write("_A=PUser_L");
            httpConn.connect();
            System.out.println("服务端相应代码：" + httpConn.getResponseCode());
            System.out.println("服务端响应消息:" + httpConn.getResponseMessage());
			/*BufferedReader bw = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String line = null;
			while((line = bw.readLine()) != null) {
				System.out.println(line);
			}*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
