package jdk.segmentdownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 网络资源分片下载
 *
 * @author dongfang.ding
 * @date 2019/6/19 10:08
 */
public class UrlFileSegmentDownload {

    class ConnectionState {
        private HttpURLConnection httpURLConnection;
        /**
         * 0, 连接可用  1 下载成功
         */
        private int status;
    }

    /**
     * 资源文件大小，守护进程会用来计算该值，单位kb
     */
    private volatile long fileSize;

    /**
     * 每次下载多少大小,在源文件大小基础上切分,单位kb;
     * 如100MB的文件，每次下载10MB，在5台服务器上分段切分；对应的fileSize=100MB, partSize=10MB, segment=5
     */
    private long partSize;

    /**
     * 分片大小, 在partSize上进行切分;
     */
    private int segment;

    /**
     * 服务器网络资源地址
     */
    private String[] serverPaths;

    /**
     * 下载完成后存放地址
     */
    private String downloadPath;

    /** 本类的线程池 */
    ExecutorService executorService = Executors.newFixedThreadPool(serverPaths.length);

    /**
     * 可用的网络资源地址,用心跳检测来保证连接的可用性
     */
    private Map<String, HttpURLConnection> connectionMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    private volatile boolean stop = false;

    /**
     * 连接正常状态码
     */
    private final List<Integer> CONNECTION_OK = Arrays.asList(200, 206);

    /**
     * 获取文件大小，如果连接还未获得，不能正确获得，则该方法会一直阻塞
     *
     * @return
     */
    public long getFileSize() {
        try {
            while (fileSize == 0) {
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.fileSize;
    }


    UrlFileSegmentDownload(String[] serverPaths, String downloadPath, int partSize) {
        this.serverPaths = serverPaths;
        this.segment = serverPaths.length;
        this.downloadPath = downloadPath;
        this.partSize = partSize;
        this.fileSize = 104857600;
    }

    /**
     * 获取远程文件大小
     *
     * @param connection 资源连接
     */
    private void getRemoteFileSize(HttpURLConnection connection) {
        try {
            lock.lock();
            while (this.fileSize == 0) {
                String contentLength = connection.getHeaderField("Content-Length");
                this.fileSize = Long.parseLong(contentLength);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 停止下载任务，下载完成后必须手动调用
     */
    public void stop() {
        this.stop = true;
    }

    /**
     * 创建http连接
     *
     * @param serverPath 服务器请求地址
     * @param method     请求方法
     * @return 返回创建的连接
     * @throws IOException
     */
    private HttpURLConnection getConnection(String serverPath, String method, Map<String, String> properties) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(serverPath).openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setRequestMethod(method);
        if (properties != null && !properties.isEmpty()) {
            properties.forEach(urlConnection::setRequestProperty);
        }
        if (!CONNECTION_OK.contains(urlConnection.getResponseCode())) {
            throw new UnknownHostException(serverPath + ": " + urlConnection.getResponseCode());
        }
        return urlConnection;
    }

    /**
     * 针对下载资源进行心跳检测，如果资源有问题，则不读取该台服务器上的资源
     *
     * @param checkInterval
     */
    public void heartCheck(long checkInterval) {
        new Thread(() -> {
            while (!stop) {
                try {
                    demonTask(checkInterval, serverPaths);
                }  finally {
                    System.out.println(connectionMap);
                    try {
                        Thread.sleep(checkInterval);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, "pool-heart-check").start();
    }

    private void demonTask(long checkInterval, String[] serverPaths) {
        System.out.println("开始心跳检测------------------------------------");
        for (String serverPath : serverPaths) {
            try {
                Map<String, String> properties = new HashMap<>();
                properties.put("Connection", "Keep-Alive");
                HttpURLConnection connection = getConnection(serverPath, "HEAD", properties);
                // 验证连接是否可用
                String date = connection.getHeaderField("date");
                getRemoteFileSize(connection);
                System.out.printf("date[%s] from [%s] \n ", date, serverPath);
                if (connectionMap.containsKey(serverPath)) {
                    if (!CONNECTION_OK.contains(connection.getResponseCode())) {
                        System.out.println("移除主机： [" + serverPath + "]");
                        connectionMap.remove(serverPath);
                    }
                } else {
                    if (CONNECTION_OK.contains(connection.getResponseCode())) {
                        System.out.println("添加主机： [" + serverPath + "]");
                        connectionMap.put(serverPath, connection);
                    }
                }
            } catch (Exception e) {
                if (connectionMap.containsKey(serverPath)) {
                    System.out.println("移除主机： [" + serverPath + "]");
                    connectionMap.remove(serverPath);
                }
            }
        }
    }

    public String download() {
        RandomAccessFile raf;
        final long size = getFileSize();
        try {
            raf = new RandomAccessFile(downloadPath, "rwd");
            raf.setLength(size);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // 处理连接在下载的过程中断了
        long average = size / segment;
        int failureTimes = 0;
        CountDownLatch countDownLatch = new CountDownLatch(serverPaths.length);
        for (int i = 1; i <= serverPaths.length; i ++) {
            String serverPath = serverPaths[i - 1];
            long startSize = (i - 1) * average + (failureTimes * average);
            long endSize = startSize + average + (failureTimes * average);
            if (i == serverPaths.length) {
                endSize = size;
            }
            DownloadTask downloadTask = new DownloadTask(serverPath, startSize, endSize, raf, countDownLatch);
            Future<Integer> submit = executorService.submit(downloadTask);
            try {
                if (submit.get() != 2) {
                    downloadTask.retry();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
            raf.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
        return downloadPath;
    }

    class DownloadTask implements Callable<Integer> {
        private String serverPath;

        private long startSize;

        private long endSize;

        private RandomAccessFile raf;

        /** 0初始化  1 读取中 2 读取完成 */
        private volatile int status;

        private CountDownLatch countDownLatch;

        DownloadTask(String serverPath, long startSize, long endSize, RandomAccessFile raf, CountDownLatch countDownLatch) {
            this.serverPath = serverPath;
            this.startSize = startSize;
            this.endSize = endSize;
            this.raf = raf;
            this.countDownLatch = countDownLatch;
        }


        /**
         * 对本类的任务进行重试
         */
        public synchronized void retry() {
            // 先对原先服务器进行重试
            if (status == 0) {
                Future<Integer> submit = executorService.submit(this);
                try {
                    if (submit.get() == 2) {
                        return;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            boolean retrySuccess = false;
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                this.serverPath = entry.getKey();
                Future<Integer> submit = executorService.submit(this);
                try {
                    if (submit.get() == 2) {
                        retrySuccess = true;
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if (!retrySuccess) {
                throw new RuntimeException("下载失败，没有服务器能提供资源下载！");
            }
        }

        @Override
        public Integer call() {
            Map<String, String> properties = new HashMap<>(6);
            properties.put("Connection", "Keep-Alive");
            properties.put("Range", "bytes=" + startSize + "-" + endSize);
            try {
                System.out.println(serverPath + "从" + startSize + "下载到" + endSize);
                HttpURLConnection conn = getConnection(serverPath, "GET", properties);
                if (CONNECTION_OK.contains(conn.getResponseCode())) {
                    status = 1;
                    // 流中断的情况要不要考虑？
                    try (InputStream inputStream = conn.getInputStream()) {
                        raf.seek(startSize);
                        byte[] buf = new byte[1024];
                        while (inputStream.read(buf) != -1) {
                            raf.write(buf);
                        }
                        status = 2;
                    } catch (Exception e) {
                        status = 0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
                return status;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String[] paths = {"http://47.88.102.56/test.file", "http://47.89.244.85/test.file", "http://47.89.209.42/test.file"};
        UrlFileSegmentDownload load = new UrlFileSegmentDownload(paths, "D:\\workSpace\\JavaDemo\\src\\main\\resources\\a.txt", 10 * 1024);
        load.heartCheck(5000);
        System.out.println("文件大小： " + load.getFileSize());
        load.download();
    }

}
