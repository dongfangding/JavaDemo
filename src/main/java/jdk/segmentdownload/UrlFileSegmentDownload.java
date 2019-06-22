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
 * 网络资源分片下载，还有本来不是作为多个服务器的不同资源在本地进行多线程下载；而是多个服务器都有一个相同的资源，然后使用多线程对不同服务器上进行分片下载；
 * 每个服务器各下载一部分，最终合成一个最终的文件；
 *
 * 必须一个网络资源下载对应创建一个新的类，不能作为单例使用；
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

    /** 是否开启打印输出 */
    private boolean isDebug;

    private AtomicInteger currentSuccessSegment = new AtomicInteger();

    private Map<Integer, List<DownloadTask>> doneTaskMap = new ConcurrentHashMap<>();

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
     * 服务器网络资源地址，必须是相同的资源在不同的服务器上
     */
    private String[] serverPaths;

    /**
     * 下载完成后存放地址
     */
    private String downloadPath;

    /** 用来停止重试线程循环的条件 */
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
        this.downloadPath = downloadPath + File.separator + getFileName();
        this.partSize = partSize;
        this.downloadExecutorService = defaultDownloadExecutorService;
        this.retryExecutorService = defaultRetryExecutorService;
        this.retryTimes = retryTimes;
        this.isDebug = isDebug;
    }

    UrlFileSegmentDownload(String[] serverPaths, String downloadPath, int partSize, int retryTimes,
                           ExecutorService downloadExecutorService, ExecutorService retryExecutorService, boolean isDebug) {
        this.serverPaths = serverPaths;
        this.downloadPath = downloadPath + File.separator + getFileName();
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
    private void getRemoteFileSize() {
        try {
            while (this.fileSize == 0) {
                lock.lock();
                if (connectionMap == null || connectionMap.isEmpty()) {
                    checkAliveConnection();
                }
                connectionMap.forEach((serverPath, connection) -> {
                    String contentLength = connection.getHeaderField("Content-Length");
                    this.fileSize = Long.parseLong(contentLength);
                });
                // 计算分段数量
                if (fileSize % partSize == 0) {
                    totalSegment = (int) (fileSize / partSize);
                } else {
                    totalSegment = (int) (fileSize / partSize) + 1;
                }
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
    private void heartCheck() {
        print("开始心跳检测");
        for (String serverPath : serverPaths) {
            try {
                Map<String, String> properties = new HashMap<>();
                HttpURLConnection connection = getConnection(serverPath, "HEAD", properties);
                String date = connection.getHeaderField("date");
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
        heartCheck();
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
        // 获取远程文件大小
        getRemoteFileSize();
        // 重试线程
        demonRetry();
        //  以最终可用连接数来平分下载量
        final int connectionSize = connectionMap.size();
        // 可用连接下载的线程是在partSize的基础上再切分的
        long average = partSize / connectionSize;
        String serverPath;
        HttpURLConnection connection;
        /**
         * 分片大小, 在partSize上进行切分;
         */
        int currSegment = 0;
        long startSize, endSize = 0;

        // 判断最终需要多少个线程来完成下载任务
        int taskCount;
        if (fileSize % average == 0) {
            taskCount = (int) (fileSize / average);
        } else {
            taskCount = (int) (fileSize / average + 1);
        }
        print(String.format("总文件大小[%d]， 每段截取大小[%d], 共分段[%d]次, 可用连接数[%d]，最终需要切分成[%d]个子任务",
                fileSize, partSize, totalSegment, connectionSize, taskCount));
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        int currTask = 0;
        while (currSegment <= totalSegment && !failureFlag) {
            currSegment ++;
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                currTask ++;
                if (currSegment > totalSegment) {
                    break;
                }
                serverPath = entry.getKey();
                // 从0开始其实是从第一个字节开始下载， 从20开始，其实是从21个字节开始；所以值虽然与上一次的endSize相同，但取值不同
                startSize = endSize;
                endSize = (startSize + average) - 1;
                if (currTask == taskCount) {
                    endSize = fileSize;
                }
                DownloadTask downloadTask = new DownloadTask(serverPath, startSize, endSize, countDownLatch);
                // FIXME
                downloadTask.setPart(currSegment, connectionSize);
                print(String.format("currSegment: [%s]，下载任务: %s)", currSegment, downloadTask));
                downloadExecutorService.execute(downloadTask);
                // 如果可用连接数大于总分段数，那么其实循环一次可用连接就处理完了；不需要重复分段
                if (endSize == fileSize) {
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
            // 下载失败标志，给主线程判断
            failureFlag = true;
            print("最终重试下载失败，调用countDown[addFailureTask]");
            // 如果最终重试失败了，则当前任务要把自己的countDown放行掉（测试时偶有出现，放行前的代码，竞争不过主线程？？）
            downloadTask.countDownLatch.countDown();
            return false;
        }
    }

    /**
     * 下载任务
     */
    class DownloadTask implements Runnable {
        /**
         * 需求想要多个线程如果下完其中一个partSize的话，需要打印第i段下载完成；实际上这个根本没有意义；
         * 最初线程分配好的任务最终也不一定就是最初的这几个线程完成的，而且先后顺序也没法保证，就算打印出来，又没啥用；
         * 而且分配任务是按照连接数来的，异常情况下，连接数是个变数；现在只能临时记录下分配任务时的这几个属性;
         * 只能每完成（总数除连接数）个就打印++i次，
         */
        private int segmentNum;
        private int currConnectionSize;
        /** 资源地址 */
        private String serverPath;
        /** 起始字节 */
        private long startSize;
        /** 终止字节 */
        private long endSize;
        /** 任务重试失败次数，重试成功后被清0 */
        private int taskFailureTimes;
        /** 用来阻塞主线程等待所有下载任务最终的下载结果 */
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

        /**
         * 单独T出来一个方法来做这个处理，将来不需要了，直接去掉这块就好
         * @param segmentNum
         * @param currConnectionSize
         */
        public void setPart(int segmentNum, int currConnectionSize) {
            this.segmentNum = segmentNum;
            this.currConnectionSize = currConnectionSize;
        }


        /**
         * 这样写，不保证顺序，按照分配任务的时候的分段来打印；如果第三段的任务先完成，就先打印第三段完成，而不是第一段完成
         */
        private void addPartAndPrint() {
            try {
                lock.lock();
                List<DownloadTask> tempList = doneTaskMap.get(this.segmentNum);
                if (tempList == null) {
                    tempList = new ArrayList<>();
                }
                tempList.add(this);
                if (tempList.size() == this.currConnectionSize) {
                    print(String.format("第[%d]块下载完成", this.segmentNum));
                }
                doneTaskMap.put(this.segmentNum, tempList);
            } finally {
                lock.unlock();
            }
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
                    print(String.format("第一次重试成功, 当前任务： %s", this));
                    countDownLatch.countDown();
                    return true;
                } else {
                    addFailureTask(this);
                }
            }
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                taskFailureTimes++;
                this.serverPath = entry.getKey();
                print(String.format("重试失败，转换服务器, 更改任务为 %s", this));
                run();
                if (status == 2) {
                    taskFailureTimes = 0;
                    print(String.format("转换服务器下载成功， 当前任务： %s", this));
                    countDownLatch.countDown();
                    return true;
                } else {
                    addFailureTask(this);
                }
            }
            // FIXME 重试失败要不要删除已经下载的数据？
            return taskFailureTimes <= retryTimes;
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
                        print("第一次下载失败");
                        addFailureTask(this);
                    }
                    return;
                } else {
                    Thread.sleep(1500);
                    // 第一次成功直接就countDown，如果不是第一次则会由retry方法来调用该方法，retry里重试成功后会有一些处理，处理结束后再countDown；
                    // 如果在这里就countDwon主线程被放行了，重试线程还在异步处理
                    if (taskFailureTimes == 0) {
                        print("调用countDown[run]");
                        countDownLatch.countDown();
                    }
                }
            } catch (InterruptedException e) {

            }*/
            Map<String, String> properties = new HashMap<>(6);
            properties.put("Range", "bytes=" + startSize + "-" + endSize);
            try {
                // 这样做是因为为了能够每写一部分数据就能看到，所以文件在每个任务中写完都调用了close方法，所以任务重复调用要重复初始化
                RandomAccessFile raf = new RandomAccessFile(downloadPath, "rws");
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
                    } catch (Exception e) {
                        status = 0;
                        if (taskFailureTimes == 0) {
                            addFailureTask(this);
                        }
                        print(String.format("任务 %s 下载异常，状态置为0", this));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (status != 2) {
                    // 只有第一次失败才添加到重试队列中，后续重试由重试方法处理
                    if (taskFailureTimes == 0) {
                        addFailureTask(this);
                    }
                } else {
                    addPartAndPrint();
                    if (taskFailureTimes == 0) {
                        countDownLatch.countDown();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] paths = {"http://localhost:8080/docs/aio.html", "http://localhost:8082/docs/aio.html"};
        String downloadPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        UrlFileSegmentDownload load = new UrlFileSegmentDownload(paths, downloadPath, 5000 * 1024, 5, true);
        long before = System.currentTimeMillis();
        load.download();
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时: " + (endTime - before));
    }
}
