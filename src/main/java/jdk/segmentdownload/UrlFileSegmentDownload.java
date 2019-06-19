package jdk.segmentdownload;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 网络资源分片下载
 *
 * @author dongfang.ding
 * @date 2019/6/19 10:08
 */
public class UrlFileSegmentDownload {
    /**
     * 分片大小
     */
    private int segment;
    /**
     * 服务器网络资源地址
     */
    private String[] serverPaths;

    /** 资源文件大小，守护进程会用来计算该值 */
    private volatile long fileSize;



    UrlFileSegmentDownload(String[] serverPaths) {
        this.serverPaths = serverPaths;
    }


    /** 可用的网络资源地址,用心跳检测来保证连接的可用性 */
    private Set<String> serverPathSet = new CopyOnWriteArraySet<>();

    /** 可用的网络资源地址,用心跳检测来保证连接的可用性 */
    private Map<String, HttpURLConnection> connectionMap = new ConcurrentHashMap<>();

    /**
     * 获取远程文件大小
     *
     * @param connection 资源连接
     */
    private void getRemoteFileSize(HttpURLConnection connection) {
        if (this.fileSize == 0) {
            String contentLength = connection.getHeaderField("Content-Length");
            this.fileSize = Long.parseLong(contentLength);
        }
    }

    /**
     * 创建http连接
     *
     * @param serverPath  服务器请求地址
     * @param method      请求方法
     * @param isKeepAlive 是否长连接
     * @return 返回创建的连接
     * @throws IOException
     */
    private HttpURLConnection getConnection(String serverPath, String method, boolean isKeepAlive) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(serverPath).openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setRequestMethod(method);
        if (isKeepAlive) {
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
        }
        if (urlConnection.getResponseCode() != 200) {
            throw new UnknownHostException();
        }
        return urlConnection;
    }


    public void heartCheck(long checkInterval) {
        for (String serverPath : serverPaths) {
            // 是一个线程监控一台主机，还是一个线程监控多台主机？
            Thread thread = new Thread(() -> {
                while (true) {
                    demonTask(checkInterval, serverPath);
                }
            }, "pool-heart-check-" + serverPath);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void demonTask(long checkInterval, String serverPath) {
        System.out.println("开始心跳检测------------------------------------");
        try {
            HttpURLConnection connection = getConnection(serverPath, "HEAD", true);
            // 验证连接是否可用
            String date = connection.getHeaderField("date");
            System.out.println("date: " + date);
            if (connectionMap.containsKey(serverPath)) {
                if (connection.getResponseCode() != 200) {
                    System.out.println("移除主机： [" + serverPath +"]");
                    connectionMap.remove(serverPath);
                }
            } else {
                if (connection.getResponseCode() == 200) {
                    System.out.println("添加主机： [" + serverPath +"]");
                    connectionMap.put(serverPath, connection);
                }
            }
            System.out.println(connectionMap);
            if (checkInterval == 0) {
                checkInterval = 5000;
            }
            Thread.sleep(checkInterval);
        } catch (Exception e) {
            if (connectionMap.containsKey(serverPath)) {
                System.out.println("移除主机： [" + serverPath +"]");
                connectionMap.remove(serverPath);
            }
            System.out.println(connectionMap);
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            demonTask(checkInterval, serverPath);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String[] paths = {"http://47.88.102.56/test.file", "http://47.89.244.85/test.file", "http://47.89.209.42/test.file", "http://www.baidu.com"};
        UrlFileSegmentDownload load = new UrlFileSegmentDownload(paths);
        load.heartCheck(5000);
        Thread.sleep(200000);
    }

}
