package jdk.segmentdownload;

import java.io.FileNotFoundException;
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

    /**
     * 资源文件大小，守护进程会用来计算该值，单位kb
     */
    private volatile long fileSize;

    /**
     * 每次下载多少大小,在源文件大小基础上切分,单位kb;
     * 如100MB的文件，每次下载10MB，在5台服务器上分段切分；对应的fileSize=100MB, partSize=10MB, segment=5
     */
    private long partSize;

    private int totalSegment;

    /**
     * 服务器网络资源地址
     */
    private String[] serverPaths;

    /**
     * 下载完成后存放地址
     */
    private String downloadPath;

    /**
     * 本类的线程池
     */
    private ExecutorService executorService;

    /**
     * 默认线程池
     */
    private ExecutorService defaultExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 可用的网络资源地址,用心跳检测来保证连接的可用性
     */
    private Map<String, HttpURLConnection> connectionMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    private volatile boolean stop = false;
    /**
     * 重试次数
     */
    private int retryTimes;
    /**
     * 重试失败次数，如果重试成功，清0
     */
    private int failureTimes;

    /**
     * 连接正常状态码
     */
    private final List<Integer> CONNECTION_OK = Arrays.asList(200, 206);

    UrlFileSegmentDownload(String[] serverPaths, String downloadPath, int partSize, int retryTimes) {
        this.serverPaths = serverPaths;
        this.downloadPath = downloadPath;
        this.partSize = partSize;
        this.executorService = defaultExecutorService;
        this.retryTimes = retryTimes;
    }

    UrlFileSegmentDownload(String[] serverPaths, String downloadPath, int partSize, int retryTimes, ExecutorService executorService) {
        this.serverPaths = serverPaths;
        this.downloadPath = downloadPath;
        this.partSize = partSize;
        this.executorService = executorService;
        this.retryTimes = retryTimes;
    }


    /**
     * 最好使用方传入自己的线程池作为全局使用，如果不是使用方自己传入的，本来为了支持多线程，该类会默认创建一个，所以如果
     * 是默认的，下载完成或失败之后需要关闭线程池，不建议这样使用，如果忘记关闭线程池或者频繁创建关闭都会过多的占用资源;
     * 如果是在web环境中，自己传入的全局线程池，不需要关闭，但如果是默认的，却不得不关闭
     */
    public void shutdownDefaultExecutor() {
        if (executorService == defaultExecutorService) {
            executorService.shutdown();
        }
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
            if (fileSize % partSize == 0) {
                totalSegment = (int) (fileSize / partSize);
            } else {
                totalSegment = (int) (fileSize / partSize) + 1;
            }
        } finally {
            lock.unlock();
        }
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
     * 心跳检测
     */
    private void demonTask() {
        System.out.println("开始心跳检测------------------------------------");
        for (String serverPath : serverPaths) {
            try {
                Map<String, String> properties = new HashMap<>();
                HttpURLConnection connection = getConnection(serverPath, "HEAD", properties);
                // 验证连接是否可用
                String date = connection.getHeaderField("date");
                getRemoteFileSize(connection);
                System.out.printf("date[%s] from [%s] \n ", date, serverPath);
                final int responseCode = connection.getResponseCode();
                if (connectionMap.containsKey(serverPath)) {
                    if (!CONNECTION_OK.contains(responseCode)) {
                        System.out.println("移除服务器资源： [" + serverPath + "]");
                        connectionMap.remove(serverPath);
                    }
                } else {
                    if (CONNECTION_OK.contains(responseCode)) {
                        System.out.println("添加服务器资源： [" + serverPath + "]");
                        connectionMap.put(serverPath, connection);
                    } else {
                        System.out.println("服务器资源[" + serverPath + "]连接异常，状态码： [" + responseCode + "]");
                    }
                }
            } catch (Exception e) {
                if (connectionMap.containsKey(serverPath)) {
                    System.out.println("移除服务器资源： [" + serverPath + "]");
                    connectionMap.remove(serverPath);
                } else {
                    System.out.println("服务器资源[" + serverPath + "]无法连接");
                }
            }
        }
    }


    /**
     * 检查可用的连接，提供重试
     */
    private void checkAliveConnection() {
        demonTask();
        if (connectionMap == null || connectionMap.isEmpty()) {
            if (failureTimes == retryTimes) {
                throw new RuntimeException("没有可用的连接");
            } else {
                failureTimes++;
                checkAliveConnection();
            }
        }
        failureTimes = 0;
    }

    public String download() {
        checkAliveConnection();
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(downloadPath, "rws");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("总文件大小： " + fileSize);
        // 以最终可用连接数来平分下载量
        final int connectionSize = connectionMap.size();
        long average;
        if (connectionSize >= totalSegment) {
            average = fileSize / connectionSize;
        } else {
            average = partSize / connectionSize;
        }
        int i = 1;
        String serverPath;
        HttpURLConnection connection;
        /**
         * 分片大小, 在partSize上进行切分;
         */
        int currSegment = 0;
        System.out.println("总分段大小： " + totalSegment);
        long startSize = 0, endSize = -1;
        while (currSegment < totalSegment) {
            List<Future<Integer>> resultList = new ArrayList<>();
            List<DownloadTask> taskList = new ArrayList<>();
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                currSegment ++;
                serverPath = entry.getKey();
                startSize = endSize + 1;
                endSize = (startSize + average);
                if (currSegment == totalSegment) {
                    endSize = fileSize;
                }
                DownloadTask downloadTask = new DownloadTask(serverPath, startSize, endSize, raf);
                System.out.println("currSegment: " + currSegment + "-" + downloadTask);
               /* Future<Integer> submit = executorService.submit(downloadTask);
                resultList.add(submit);
                taskList.add(downloadTask);*/
                downloadTask.call();
                i ++;
            }
            try {
                // 有些地方需要闭锁来保证下载任务确实下载完成，但提交任务的时候不需要，可是在这里get才会真正执行线程任务，依然导致下载任务阻塞
                for (int i1 = 0; i1 < resultList.size(); i1++) {
                    Future<Integer> integerFuture = resultList.get(i1);
                    try {
                        if (integerFuture.get() != 2) {
                            taskList.get(i1).retry();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (currSegment == totalSegment) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    shutdownDefaultExecutor();
                }
            }
            // 如果可用连接数大于总分段数，那么其实循环一次可用连接就处理完了；不需要重复分段
            if (connectionSize >= totalSegment) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                shutdownDefaultExecutor();
                break;
            }
        }
        return downloadPath;
    }

    class DownloadTask implements Callable<Integer> {
        private String serverPath;

        private long startSize;

        private long endSize;

        private RandomAccessFile raf;

        /**
         * 0初始化  1 读取中 2 读取完成
         */
        private volatile int status;

        DownloadTask(String serverPath, long startSize, long endSize, RandomAccessFile raf) {
            this.serverPath = serverPath;
            this.startSize = startSize;
            this.endSize = endSize;
            try {
                this.raf = new RandomAccessFile(downloadPath, "rws");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "DownloadTask{" +
                    "serverPath='" + serverPath + '\'' +
                    ", startSize=" + startSize +
                    ", endSize=" + endSize +
                    ", status=" + status +
                    '}';
        }

        /**
         * 对本类的任务进行重试
         */
        public synchronized void retry() {
            // 先对原先服务器进行重试
            System.out.println(Thread.currentThread() + "开始重试-----------------");
            if (status == 0) {
                final int newStatus = call();
                if (newStatus == 2) {
                    System.out.println(Thread.currentThread() + "第一次重试成功: " + this);
                    return;
                }
            }
            boolean retrySuccess = false;
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                this.serverPath = entry.getKey();
                System.out.println(Thread.currentThread() + "重试失败，转换服务器: " + this);
                final int newStatus = call();
                if (newStatus == 2) {
                    retrySuccess = true;
                    System.out.println(Thread.currentThread() + "转换服务器下载成功: " + this);
                    break;
                }
            }
            // FIXME 重试失败要不要删除已经下载的数据？
            if (!retrySuccess) {
                try {
                    raf.close();
                    shutdownDefaultExecutor();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("下载失败，没有服务器能提供资源下载！");
            }
        }

        @Override
        public Integer call() {
            Map<String, String> properties = new HashMap<>(6);
            properties.put("Range", "bytes=" + startSize + "-" + endSize);
            try {
                System.out.println(Thread.currentThread() + serverPath + "从" + startSize + "下载到" + endSize);
                HttpURLConnection conn = getConnection(serverPath, "GET", properties);
                if (CONNECTION_OK.contains(conn.getResponseCode())) {
                    System.out.println(Thread.currentThread() + "返回大小： " + conn.getHeaderField("Content-Length"));
                    status = 1;
                    System.out.println(Thread.currentThread() + "连接正常...状态置为1");
                    try (InputStream inputStream = conn.getInputStream()) {
                        System.out.println("设置起始点： " + startSize);
                        raf.seek(startSize);
                        byte[] buf = new byte[1024 * 4];
                        long count;
                        final int bufLength = buf.length;
                        // 由于缓冲数组是1024，如果最后一次不足1024，而写入1024数组长度的话，会有很多空，所以计算出来最后一次数组中有效数据的长度
                        if ((endSize - startSize) % bufLength == 0) {
                            count = (endSize - startSize) / bufLength;
                        } else {
                            count = (endSize - startSize) / bufLength + 1;
                        }
                        int loopIndex = 0;
                        while (inputStream.read(buf) != -1) {
                            loopIndex ++;
                            // 最后一次只写入有效数据的长度
                            if (loopIndex == count) {
                                raf.write(buf, 0, (int) (endSize - startSize) % bufLength);
                            } else {
                                raf.write(buf);
                            }
                            raf.write(buf);
                        }
                        raf.close();
                        status = 2;
                        System.out.println(Thread.currentThread() + "下载完成，状态置为2");
                    } catch (Exception e) {
                        status = 0;
                        System.out.println(Thread.currentThread() + "下载异常，状态置为0");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return status;
        }
    }

    public static void main(String[] args) {
        String[] paths = {"http://localhost:8080/docs/aio.html", "http://localhost:8080/docs/aio1.html"};
        UrlFileSegmentDownload load = new UrlFileSegmentDownload(paths, "D:\\workSpace\\JavaDemo\\src\\main\\resources\\a.txt", 3 * 1024, 5);
        load.download();
    }
}
