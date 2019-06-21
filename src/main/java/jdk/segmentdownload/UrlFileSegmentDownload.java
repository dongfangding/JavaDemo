package jdk.segmentdownload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 网络资源分片下载
 * 本来必须一个网络资源下载创建一个新的类，不能作为单例使用；
 * 其中用来作为监听重试队列的线程和用来处理下载任务的线程池最好初始化的时候传入两个全局的线程池来使用;
 *
 * @author dongfang.ding
 * @date 2019/6/19 10:08
 */
public class UrlFileSegmentDownload {

    /**
     * 资源文件大小，守护进程会用来计算该值，单位byte
     */
    private volatile long fileSize;

    private boolean isDebug;

    private AtomicInteger currentSuccessSegment = new AtomicInteger();

    /**
     * 每次下载多少大小,在源文件大小基础上切分,单位byte;
     * 如100MB的文件，每次下载10MB，在5台服务器上分段切分；对应的fileSize=100MB, partSize=10MB, segment=5
     */
    private long partSize;

    /**
     * 根据fileSize和partSize计算出来的需要分段几次，能够下载完成
     */
    private int totalSegment;

    /**
     * 下载是否失败，由于重试任务使用线程来控制，所以
     */
    private volatile boolean failureFlag = false;

    /**
     * 服务器网络资源地址
     */
    private String[] serverPaths;

    /**
     * 下载完成后存放地址
     */
    private String downloadPath;


    private volatile boolean demonRetryStop = false;

    /**
     * 用来下载具体任务的线程池，最好自己传入一个全局的线程池
     */
    private ExecutorService downloadExecutorService;


    /**
     * 用来监听重试队列的线程池，最好自己传入一个全局的线程池
     */
    private ExecutorService retryExecutorService;

    /**
     * 默认下载任务的线程池，在这里定义主要是为了方便在下载完成后判断如果使用的是默认线程池，要吧线程池关闭掉；
     * 所以最好传入自定义的全局线程池来使用
     */
    private ExecutorService defaultDownloadExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 默认用来监听重试队列的线程池，在这里定义主要是为了方便在下载完成后判断如果使用的是默认线程池，要吧线程池关闭掉；
     * 所以最好传入自定义的全局线程池来使用
     */
    private ExecutorService defaultRetryExecutorService = Executors.newSingleThreadExecutor();

    /**
     * 可用的网络资源地址,用心跳检测来保证连接的可用性
     */
    private Map<String, HttpURLConnection> connectionMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    /**
     * 为了重试线程与失败队列完成等待唤醒功能而搞出来的一个锁对象
     */
    private final Object object = new Object();

    /**
     * 重试任务队列
     */
    private Queue<DownloadTask> failureTaskQueue = new LinkedBlockingQueue<>();

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

    UrlFileSegmentDownload(String[] serverPaths, String downloadPath, int partSize, int retryTimes, boolean isDebug) {
        this.serverPaths = serverPaths;
        this.downloadPath = downloadPath;
        this.partSize = partSize;
        this.downloadExecutorService = defaultDownloadExecutorService;
        this.retryExecutorService = defaultRetryExecutorService;
        this.retryTimes = retryTimes;
        this.isDebug = isDebug;
    }

    UrlFileSegmentDownload(String[] serverPaths, String downloadPath, int partSize, int retryTimes,
                           ExecutorService downloadExecutorService, ExecutorService retryExecutorService, boolean isDebug) {
        this.serverPaths = serverPaths;
        this.downloadPath = downloadPath;
        this.partSize = partSize;
        this.downloadExecutorService = downloadExecutorService;
        this.retryExecutorService = retryExecutorService;
        this.retryTimes = retryTimes;
        this.isDebug = isDebug;
    }

    private String getFileName() {
        if (serverPaths.length > 0) {
            return serverPaths[0].substring(serverPaths[0].lastIndexOf("/"));
        }
        return null;
    }

    /**
     * 最好使用方传入自己的线程池作为全局使用，如果不是使用方自己传入的，本来为了支持多线程，该类会默认创建，所以如果
     * 是默认的，下载完成或失败之后需要关闭线程池，不建议这样使用，如果忘记关闭线程池或者频繁创建关闭都会过多的占用资源;
     * 如果是在web环境中，自己传入的全局线程池，不需要关闭，但如果是默认的，却不得不关闭
     */
    public void shutdownDefaultExecutor() {
        if (downloadExecutorService == defaultDownloadExecutorService) {
            downloadExecutorService.shutdown();
        }
        if (retryExecutorService == defaultRetryExecutorService) {
            retryExecutorService.shutdown();
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
            // 计算分段数量
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
        print("开始心跳检测");
        for (String serverPath : serverPaths) {
            try {
                Map<String, String> properties = new HashMap<>();
                HttpURLConnection connection = getConnection(serverPath, "HEAD", properties);
                String date = connection.getHeaderField("date");
                getRemoteFileSize(connection);
                print(String.format("date[%s] from [%s]", date, serverPath));
                final int responseCode = connection.getResponseCode();
                if (connectionMap.containsKey(serverPath)) {
                    if (!CONNECTION_OK.contains(responseCode)) {
                        print(String.format("移除服务器资源[%s]", serverPath));
                        connectionMap.remove(serverPath);
                    }
                } else {
                    if (CONNECTION_OK.contains(responseCode)) {
                        print(String.format("添加服务器资源[%s]", serverPath));
                        connectionMap.put(serverPath, connection);
                    } else {
                        print(String.format("服务器资源[%s]连接异常，状态码[%s]", serverPath, responseCode));
                    }
                }
            } catch (Exception e) {
                if (connectionMap.containsKey(serverPath)) {
                    print(String.format("移除服务器资源[%s]", serverPath));
                    connectionMap.remove(serverPath);
                } else {
                    print(String.format("服务器资源[%s]无法连接", serverPath));
                }
            }
        }
    }

    /**
     * 打印方法,提供一个全局属性isDebug决定是否需要打印
     *
     * @param msg
     */
    private void print(String msg) {
        if (isDebug) {
            System.out.printf("[%s]: %s", Thread.currentThread(), msg);
            System.out.println();
            System.out.println();
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
        // 检查可用连接
        checkAliveConnection();
        // 重试线程
        demonRetry();
        //  以最终可用连接数来平分下载量
        final int connectionSize = connectionMap.size();
        long average;
        if (connectionSize >= totalSegment) {
            average = fileSize / connectionSize;
        } else {
            average = partSize / connectionSize;
        }
        String serverPath;
        HttpURLConnection connection;
        /**
         * 分片大小, 在partSize上进行切分;
         */
        int currSegment = 0;
        print(String.format("总文件大小[%d]， 每段截取大小[%d], 共分段[%d]次", fileSize, partSize, totalSegment));
        long startSize, endSize = 0;
        // 只有下载成功才调用countDown
        CountDownLatch countDownLatch = new CountDownLatch(totalSegment);
        while (currSegment < totalSegment && !failureFlag) {
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                currSegment++;
                serverPath = entry.getKey();
                // 从0开始其实是从第一个字节开始下载， 从20开始，其实是从21个字节开始；所以值虽然与上一次的endSize相同，但取值不同
                startSize = endSize;
                endSize = (startSize + average) - 1;
                if (currSegment == totalSegment) {
                    endSize = fileSize;
                }
                DownloadTask downloadTask = new DownloadTask(serverPath, startSize, endSize, countDownLatch);
                print(String.format("currSegment: [%s]，下载任务: %s)", currSegment, downloadTask));
                downloadExecutorService.execute(downloadTask);
                // 如果可用连接数大于总分段数，那么其实循环一次可用连接就处理完了；不需要重复分段
                if (totalSegment == 1) {
                    break;
                }
            }
            // 如果可用连接数大于总分段数，那么其实循环一次可用连接就处理完了；不需要重复分段
            if (connectionSize >= totalSegment) {
                break;
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shutdownDefaultExecutor();
        stopDemonRetry();
        System.out.println("======================================主线程============================================");
        if (failureFlag) {
            throw new RuntimeException("下载失败");
        }
        return downloadPath;
    }

    /**
     * 启动线程对失败的任务队列进行任务重试
     */
    public void demonRetry() {
        retryExecutorService.execute(() -> {
            while (!demonRetryStop) {
                while (failureTaskQueue.size() > 0 && !demonRetryStop) {
                    DownloadTask task = failureTaskQueue.poll();
                    if (task != null && !demonRetryStop) {
                        try {
                            downloadExecutorService.execute(task::retry);
                        } catch (Exception e) {
                            failureFlag = true;
                        }
                    }
                }
                try {
                    synchronized (object) {
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 停止重试线程
     */
    private void stopDemonRetry() {
        demonRetryStop = true;
        synchronized (object) {
            object.notify();
        }
    }


    /**
     * 添加重试任务队列
     *
     * @param downloadTask
     */
    private boolean addFailureTask(DownloadTask downloadTask) {
        if (!failureFlag && downloadTask.taskFailureTimes < retryTimes) {
            failureTaskQueue.offer(downloadTask);
            synchronized (object) {
                object.notify();
            }
            return true;
        } else {
            downloadTask.countDownLatch.countDown();
            print("调用countDown[" + System.currentTimeMillis() + "]");
            failureFlag = true;
            return false;
        }
    }

    /**
     * 下载任务
     */
    class DownloadTask implements Runnable {
        private String serverPath;

        private long startSize;

        private long endSize;

        private int taskFailureTimes;

        private CountDownLatch countDownLatch;

        /**
         * 0初始化  1 读取中 2 读取完成
         */
        private volatile int status;

        DownloadTask(String serverPath, long startSize, long endSize, CountDownLatch countDownLatch) {
            this.serverPath = serverPath;
            this.startSize = startSize;
            this.endSize = endSize;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public String toString() {
            return "DownloadTask{" +
                    "serverPath='" + serverPath + '\'' +
                    ", startSize=" + startSize +
                    ", endSize=" + endSize +
                    ", status=" + status +
                    ", taskFailureTimes=" + taskFailureTimes +
                    '}';
        }

        /**
         * 对本类的任务进行重试
         */
        public synchronized boolean retry() {
            if (failureFlag || demonRetryStop) {
                return false;
            }
            // 先对原先服务器进行重试
            if (taskFailureTimes == 0) {
                taskFailureTimes++;
                run();
                if (status == 2) {
                    taskFailureTimes = 0;
                    countDownLatch.countDown();
                    print("调用countDown[" + System.currentTimeMillis() + "]");
                    print(String.format("第一次重试成功, 当前任务： %s", this));
                    return true;
                } else {
                    addFailureTask(this);
                    return false;
                }
            } else {
                for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                    taskFailureTimes++;
                    this.serverPath = entry.getKey();
                    print(String.format("重试失败，转换服务器, 更改任务为 %s", this));
                    run();
                    if (status == 2) {
                        print(String.format("转换服务器下载成功， 当前任务： %s", this));
                        countDownLatch.countDown();
                        print("调用countDown[" + System.currentTimeMillis() + "]");
                        return true;
                    } else {
                        addFailureTask(this);
                        return false;
                    }
                }
            }
            // FIXME 重试失败要不要删除已经下载的数据？
            if (taskFailureTimes > retryTimes) {
                return false;
            }
            return true;
        }

        @Override
        public void run() {
            /*try {
                status = new Random().nextInt(50);
                Thread.sleep(1500);
                print("status=" + status);
                if (status != 2) {
                    // 只有第一次失败才添加到重试队列中，后续重试由重试方法处理
                    if (taskFailureTimes == 0) {
                        addFailureTask(this);
                    }
                } else {
                    Thread.sleep(1500);
                    countDownLatch.countDown();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                // 只有第一次失败才添加到重试队列中，后续重试由重试方法处理
                if (taskFailureTimes == 0) {
                    addFailureTask(this);
                }
            }*/
            Map<String, String> properties = new HashMap<>(6);
            properties.put("Range", "bytes=" + startSize + "-" + endSize);
            try {
                // 这样做是因为为了能够每写一部分数据就能看到，所以文件在每个任务中写完都调用了close方法，所以任务重复调用要重复初始化
                if (new File(downloadPath + File.separator + getFileName()).exists()) {
                    new File(downloadPath + File.separator + getFileName()).delete();
                }
                RandomAccessFile raf = new RandomAccessFile(downloadPath + File.separator + getFileName(), "rws");
                print(String.format("%s从[%s]下载到[%s]", serverPath, startSize, endSize));
                HttpURLConnection conn = getConnection(serverPath, "GET", properties);
                if (CONNECTION_OK.contains(conn.getResponseCode())) {
                    status = 1;
                    print(String.format("任务 %s 连接正常...状态置为1", this));
                    try (InputStream inputStream = conn.getInputStream()) {
                        print(String.format("设置文件写入点： %s", startSize));
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
                        }
                        // 每个任务写的内容都能及时看到
                        raf.close();
                        status = 2;
                        print(String.format("任务 %s 下载完成，状态置为2", this));
                        int i = currentSuccessSegment.incrementAndGet();
                        if (i % connectionMap.size() == 0) {
                            print(String.format("第%s块下载完成!", i / connectionMap.size()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        status = 0;
                        if (taskFailureTimes == 0) {
                            addFailureTask(this);
                        }
                        print(String.format("任务 %s 下载异常，状态置为0", this));
                    }
                }
            } catch (IOException e) {
                // 只有第一次失败才添加到重试队列中，后续重试由重试方法处理
                if (taskFailureTimes == 0) {
                    addFailureTask(this);
                }
            } finally {
                if (status != 2) {
                    // 只有第一次失败才添加到重试队列中，后续重试由重试方法处理
                    if (taskFailureTimes == 0) {
                        addFailureTask(this);
                    }
                } else {
                    countDownLatch.countDown();
                    print("调用countDown[" + System.currentTimeMillis() + "]");
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] paths = {"http://localhost:8080/docs/aio.html", "http://localhost:8081/docs/aio.html"};
        UrlFileSegmentDownload load = new UrlFileSegmentDownload(paths, "D:\\workSpace\\JavaDemo\\src\\main\\resources", 2 * 1024, 5, true);
        long before = System.currentTimeMillis();
        load.download();
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时: " + (endTime - before));
    }
}
