package jdk.segmentdownload;

import jdk.secret.MD5Util;

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
 *
 * 对多台服务器上的同一个文件进行分片下载
 *
 * 网络资源分片下载，本类不是作为多个服务器的不同资源在本地进行多线程下载；而是多个服务器都有一个相同的资源，然后使用多线程对不同服务器上进行分片下载；
 * 每个服务器各下载一部分，最终合成一个最终的文件；
 *
 * 比如有一个文件是100MB，在三台服务器上， 每次下载10MB，那么就会被分成10段；每一段的下载量是可用连接来平分的，也就是三个线程分别下载 3、3、4MB共同来完成
 * 10MB的下载；然后就是第二段的任务分配，依次类推；如果中途有连接中断导致下载失败，要能够把失败的任务转移到另外一台服务器上重新下载，最终只要能够
 * 有一个可用连接把任务下载完成，就可以；然后把这些下载合成一个最终的文件；
 *
 * <p>
 * 必须一个网络资源下载对应创建一个新的类，不能作为单例使用；
 * 其中用来作为监听重试队列的线程和用来处理下载任务的线程池最好初始化的时候传入两个全局的线程池来使用;
 * <p>
 * 在Spring中推荐使用方式，首先在配置类中配置{@code Bean}
 * <pre class="code">
 * &#064;Configuration
 * public class Configuration {
 *
 *     &#064;Bean
 *     public ExecutorService downloadExecutorService {
 *         return ......
 *     }
 *
 *     &#064;Bean
 *     public ExecutorService retryExecutorService {
 *         return ......
 *     }
 *
 *     &#064;Bean
 *     &#064;Scope("prototype")
 *     public UrlFileSegmentDownload urlFileSegmentDownload(ExecutorService downloadExecutorService, ExecutorService retryExecutorService) {
 *          new UrlFileSegmentDownload(10 * 1024 * 1024, 5, 200, false, downloadExecutorService, retryExecutorService);
 *     }
 * }
 *
 * 下载类使用
 * &#064;Service
 * public class DownloadService {
 *     &#064;Autowired
 *     private UrlFileSegmentDownload urlFileSegmentDownload;
 *
 *     public void download() {
 *         String[] paths = {"http://localhost:8080/docs/changelog.html", "http://localhost:8081/docs/changelog.html", "http://aaa.2121.com/"};
 *         String downloadPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
 *         String download = load.download(paths, downloadPath);
 *     }
 * }
 *
 * </pre>
 *
 * @author dongfang.ding
 * @date 2019/6/19 10:08
 */
public class UrlFileSegmentDownload {

    /**
     * 资源文件大小，守护进程会用来计算该值，单位byte
     */
    private volatile long fileSize;

    /**
     * 是否开启打印输出
     */
    private boolean debug;

    /**
     * 已完成下载任务段
     */
    private Map<Integer, List<DownloadTask>> doneTaskMap = new ConcurrentHashMap<>();

    /**
     * 共完成多少个子任务的数量
     */
    private AtomicInteger doneTaskNum = new AtomicInteger();

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
     * 下载完成后存放路径
     */
    private String downloadPath;

    /**
     * 用来停止重试线程循环的条件
     */
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

    /**
     * 全局锁对象
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 为了重试线程与失败队列完成等待唤醒功能而搞出来的一个锁对象
     */
    private final Object object = new Object();

    /**
     * 重试任务队列,所有执行失败的下载任务都会被放到队列中，直至超出重试次数
     */
    private Queue<DownloadTask> failureTaskQueue = new LinkedBlockingQueue<>();

    /**
     * 重试次数，重试可用连接以及重试失败任务次数都是通过这个属性来指定的
     */
    private int retryTimes;

    /**
     * 连接超时时间
     */
    private int connectionTimeOut;

    /**
     * 已经重试失败次数，如果重试成功，清0
     */
    private int failureTimes;

    /**
     * 连接正常状态码,因为牵扯到分片，如果请求头加了Range属性，返回的状态码就是206了
     */
    private final List<Integer> CONNECTION_OK = Arrays.asList(200, 206);

    /**
     * 设置连接超时时间
     *
     * @param connectionTimeOut
     */
    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * 设置是否开启打印
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 设置每次分段文件下载大小
     *
     * @param partSize
     */
    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    /**
     * 设置失败重试次数
     *
     * @param retryTimes
     */
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    /**
     * 设置下载线程池
     *
     * @param downloadExecutorService
     */
    public void setDownloadExecutorService(ExecutorService downloadExecutorService) {
        this.downloadExecutorService = downloadExecutorService;
    }

    /**
     * 设置重试队列监听线程池
     *
     * @param retryExecutorService
     */
    public void setRetryExecutorService(ExecutorService retryExecutorService) {
        this.retryExecutorService = retryExecutorService;
    }


    /**
     * 推荐使用的构造函数，传入全局的任务下载调度线程池与重试队列守护线程池
     * 只初始化核心配置参数，其他与下载相关的属性通过另外下载方法指定；这种方式方便将来这个类应用于Spring的Bean的Prototype作用域配置就好;
     *
     * @param partSize                每次下载文件大小
     * @param retryTimes              失败重试次数
     * @param connectionTimeOut       连接超时时间
     * @param isDebug                 是否开启打印输出
     * @param downloadExecutorService 任务下载调度线程池
     * @param retryExecutorService    重试队列守护线程池
     */
    public UrlFileSegmentDownload(long partSize, int retryTimes, int connectionTimeOut, boolean isDebug,
                           ExecutorService downloadExecutorService, ExecutorService retryExecutorService) {
        this.partSize = partSize;
        this.retryTimes = retryTimes;
        this.connectionTimeOut = connectionTimeOut;
        this.debug = isDebug;
        this.downloadExecutorService = downloadExecutorService;
        this.retryExecutorService = retryExecutorService;
    }

    /**
     * 未指定重试队列守护线程池是与下载任务线程池，最好不要用这个，测试用
     * 只初始化核心配置参数，其他与下载相关的属性通过另外下载方法指定；这种方式方便将来这个类应用于Spring的Bean的Prototype作用域配置就好;
     *
     * @param partSize          每次下载文件大小
     * @param retryTimes        失败重试次数
     * @param connectionTimeOut 连接超时时间
     * @param isDebug           是否开启打印输出
     */
    public UrlFileSegmentDownload(long partSize, int retryTimes, int connectionTimeOut, boolean isDebug) {
        this.partSize = partSize;
        this.retryTimes = retryTimes;
        this.connectionTimeOut = connectionTimeOut;
        this.debug = isDebug;
        this.downloadExecutorService = defaultDownloadExecutorService;
        this.retryExecutorService = defaultRetryExecutorService;
    }

    /**
     * 获取资源中的文件名
     *
     * @return
     */
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
    private void shutdownDefaultExecutor() {
        if (downloadExecutorService == defaultDownloadExecutorService) {
            downloadExecutorService.shutdown();
        }
        if (retryExecutorService == defaultRetryExecutorService) {
            retryExecutorService.shutdown();
        }
    }


    /**
     * 初始化下载相关参数
     *
     * @param serverPaths  服务器网络资源地址，必须是相同的资源在不同的服务器上
     * @param downloadPath 下载完成后存放路径
     */
    private void init(String[] serverPaths, String downloadPath) {
        this.serverPaths = serverPaths;
        this.downloadPath = downloadPath + File.separator + getFileName();
    }

    /**
     * 获取远程文件大小
     */
    private void getRemoteFileSize() {
        lock.lock();
        try {
            while (this.fileSize == 0) {
                if (connectionMap == null || connectionMap.isEmpty()) {
                    checkAliveConnection();
                }
                connectionMap.forEach((serverPath, connection) -> {
                    try {
                        if (this.fileSize == 0) {
                            String contentLength = connection.getHeaderField("Content-Length");
                            this.fileSize = Long.parseLong(contentLength);
                            this.partSize = partSize > fileSize ? fileSize : partSize;
                        }
                    } catch (Exception e) {
                    }
                });
                // 计算分段数量
                if (fileSize % partSize == 0) {
                    totalSegment = (int) (fileSize / partSize);
                } else {
                    totalSegment = (int) (fileSize / partSize) + 1;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        urlConnection.setConnectTimeout(connectionTimeOut);
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
     * 心跳检测,这个也可以做成线程每隔多久去检测一次来调用这个；但目前的做法是下载前先获取一次，下载异常再获得；
     * 没有做成循环检测；唯一的问题是，下载异常后有连接可用，连接异常后果断时间有恢复，不会去识别最新的连接；
     * 当然如果已有的连接都挂了，还是会去重新获取最新的；所以只会造成下载过程中出现异常又恢复丢失连接，而不会出现丢失全部连接影响下载任务
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
                    print(String.format("出现异常 [%s], 移除服务器资源[%s]", serverPath, e.toString()));
                    connectionMap.remove(serverPath);
                } else {
                    print(String.format("服务器资源[%s]无法连接: [%s]", serverPath, e.toString()));
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
        if (debug) {
            System.out.printf("[%s]: %s", Thread.currentThread(), msg);
            System.out.println();
            System.out.println();
        }
    }


    /**
     * 检查可用的连接，提供重试,这里会有一个问题，在下载前就检查可用连接，如果在连接属性上的超时时间过长，再加上重试次数的原因，
     * 如果正好有连接不可用，这一步就会耽误过多的时间；
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


    /**
     * 下载方法
     * @param serverPaths  服务器网络资源地址，必须是相同的资源在不同的服务器上
     * @param downloadPath 下载完成后存放路径
     * @return 返回最终下载的文件完成路径包含文件名
     */
    public String download(String[] serverPaths, String downloadPath) {
        // 初始化下载相关参数
        init(serverPaths, downloadPath);
        // 获取远程文件大小
        getRemoteFileSize();
        // 开启重试队列守护线程
        demonRetry();
        // 因可用连接大小在下载过程中是个变量（如果下载中途有服务器出现故障）,所以先预先获取分配任务时的可用连接数，
        // 后续都是根据这个值来分配任务的，任务分配好后再变化就不会有影响了
        final int connectionSize = connectionMap.size();
        // 一个partSize的分段大小需要当前可用连接数个线程再切分来完成下载
        long average = partSize / connectionSize;
        String serverPath;
        long startSize, endSize = 0;
        int taskCount;
        // 判断最终需要多少个线程子任务来完成下载任务
        if (partSize >= fileSize) {
            taskCount = connectionSize;
        } else if (fileSize % average == 0) {
            taskCount = (int) (fileSize / average);
        } else {
            taskCount = (int) (fileSize / average) + 1;
        }
        // 由于average算出来的数据会舍去余数，最终结果可能会大于总循环数，但其实下面会保证在循环内把多出来的任务量都分配给每个分段的最后一个任务上了
        if (taskCount > connectionSize * totalSegment) {
            taskCount = connectionSize * totalSegment;
        }
        print(String.format("总文件大小[%d]， 每段截取大小[%d], 共分段[%d]次, 可用连接数[%d]，最终需要切分成[%d]个子任务, 平均每" +
                "个子任务需要下载[%d]个字节，", fileSize, partSize, totalSegment, connectionSize, taskCount, average));
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
        // TODO 后面在看情况决定要不要暴露一个方法允许自定义文件名吧，现在为了能准确覆盖，写之前先删除之前下载保存的文件
        if (new File(downloadPath).exists()) {
            new File(downloadPath).delete();
        }
        int currTask = 0;
        // 每循环完一个轮次的可用连接数，才是完成了一个partSize， currSegment才+1
        for (int currSegment = 1; currSegment <= totalSegment && !failureFlag; currSegment++) {
            for (Map.Entry<String, HttpURLConnection> entry : connectionMap.entrySet()) {
                currTask ++;
                serverPath = entry.getKey();
                // 从0开始其实是从第一个字节开始下载， 从20开始，其实是从21个字节开始；所以值虽然与上一次的endSize相同，但取值不同
                startSize = endSize;
                // 如果是最后一个任务，则将剩余的空间指向文件总大小
                if (currTask == taskCount) {
                    endSize = fileSize;
                }
                // 如果不是最后一个任务，但却是每个分段的最后一个子任务，则把当前子任务的剩余大小指向endSize
                else if (currTask % connectionSize == 0) {
                    endSize = partSize - (average * (connectionSize - 1)) + endSize;
                } else {
                    // 如果不是最后一个任务，也不是每个分段的最后一个任务，则endSize就是正常的在上一个任务的结束点加上平均任务字节数即可
                    endSize = (startSize + average);
                }
                if (endSize > fileSize) {
                    endSize = fileSize;
                }
                // 是否可以复用connectionMap里存的连接呢？但是如果异常切换服务器下载，又得重新根据serverPath获取，还得传入连接，因此暂时不准备这么做了
                DownloadTask downloadTask = new DownloadTask(serverPath, startSize, endSize, countDownLatch);
                // FIXME 感觉这一块功能费力又没意义
                downloadTask.setPart(currSegment, connectionSize);
                print(String.format("currSegment: [%s]，下载任务: %s)", currSegment, downloadTask));
                downloadExecutorService.execute(downloadTask);
                // 处理到最后直接跳回,因为有可能最后一次的分段只要一个子任务就完成了，没必要再循环多次了
                if (endSize >= fileSize) {
                    break;
                }
            }
            // 如果可用连接数大于总任务数，那么其实循环一次可用连接就处理完了；不需要重复分段
            if (connectionSize >= taskCount) {
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
        return this.downloadPath;
    }

    /**
     * 启动线程对失败的任务队列进行任务重试
     */
    private void demonRetry() {
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
         * @see DownloadTask#setPart(int, int)
         */
        private int segmentNum;
        /**
         * @see DownloadTask#setPart(int, int)
         */
        private int currConnectionSize;
        /**
         * 单个资源地址
         */
        private String serverPath;
        /**
         * 起始字节
         */
        private long startSize;
        /**
         * 终止字节
         */
        private long endSize;
        /**
         * 任务重试失败次数，重试成功后被清0
         */
        private int taskFailureTimes;
        /**
         * 用来阻塞主线程等待所有下载任务最终的下载结果
         */
        private CountDownLatch countDownLatch;

        /**
         * 0初始化  1 读取中 2 读取完成
         */
        private volatile int status;

        private final int STATUS_INIT = 0;
        private final int STATUS_DOING = 1;
        private final int STATUS_DONE = 2;


        DownloadTask(String serverPath, long startSize, long endSize, CountDownLatch countDownLatch) {
            this.serverPath = serverPath;
            this.startSize = startSize;
            this.endSize = endSize;
            this.countDownLatch = countDownLatch;
        }

        /**
         * 需求想要多个线程如果下完其中一个partSize的话，需要打印第i段下载完成；实际上这个应该是没有意义的；
         * 最初线程分配好的任务最终也不一定就是最初的这几个线程完成的，而且先后顺序也没法保证，就算打印出来，又没啥用；
         * 而且分配任务是按照连接数来的，异常情况下，连接数是个变数；现在只能临时记录下分配任务时的这几个属性;
         * <p>
         * 两种效果，一种是每完成（总数除连接数）个就打印++i次，这样保证打印的第i段完成是顺序递增的
         * 还有一种是按照最初任务分配的段来确定，当时分配的第几段，这几段的几个线程全部完成就打印这个段完成；但是由于线程调度
         * 原因，这样可能会发生，显示第三段完成，然后才是第一段完成，目前采用的是这种方式
         * <p>
         * 单独T出来一个方法来做这个处理，将来不需要了，直接去掉这块就好
         *
         * @param segmentNum         该任务所属分段
         * @param currConnectionSize 分配任务时是按照多少可用连接数来分的
         */
        public void setPart(int segmentNum, int currConnectionSize) {
            this.segmentNum = segmentNum;
            this.currConnectionSize = currConnectionSize;
        }


        /**
         * 这样写，不保证顺序，按照分配任务的时候的分段来打印；如果第三段的任务先完成，就先打印第三段完成，而不是第一段完成
         */
        private void addPartAndPrint() {
            lock.lock();
            try {
                List<DownloadTask> tempList = doneTaskMap.get(this.segmentNum);
                if (tempList == null) {
                    tempList = new ArrayList<>();
                }
                tempList.add(this);
                int andIncrement = doneTaskNum.incrementAndGet();
                if (tempList.size() == this.currConnectionSize) {
                    print(String.format("第[%d]块下载完成, 当前实际共下载完成[%d]段", this.segmentNum, andIncrement / this.currConnectionSize));
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
                if (status == STATUS_DONE) {
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
                if (status == STATUS_DONE) {
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
            Map<String, String> properties = new HashMap<>(6);
            properties.put("Range", "bytes=" + startSize + "-" + endSize);
            try {
                // 这样做是因为为了能够每写一部分数据就能看到，所以文件在每个任务中写完都调用了close方法，所以任务重复调用要重复初始化
                RandomAccessFile raf = new RandomAccessFile(downloadPath, "rws");
                print(String.format("%s从[%s]下载到[%s]", serverPath, startSize, endSize));
                HttpURLConnection conn = getConnection(serverPath, "GET", properties);
                if (CONNECTION_OK.contains(conn.getResponseCode())) {
                    print(String.format("返回数据大小： [%s]", conn.getHeaderField("Content-Length")));
                    status = STATUS_DOING;
                    print(String.format("任务 %s 连接正常...状态置为1", this));
                    try (InputStream inputStream = conn.getInputStream()) {
                        print(String.format("设置文件写入点： %s", startSize));
                        raf.seek(startSize);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) != -1) {
                            raf.write(buf, 0, len);
                        }
                        raf.close();
                        // 每个任务写的内容都能及时看到
                        status = STATUS_DONE;
                        print(String.format("任务 %s 下载完成，状态置为2", this));
                    } catch (Exception e) {
                        status = STATUS_INIT;
                        if (taskFailureTimes == 0) {
                            addFailureTask(this);
                        }
                        print(String.format("任务 %s 下载异常，状态置为0, 异常信息： [%s]", this, e.toString()));
                    }
                }
            } catch (IOException e) {
                status = STATUS_INIT;
                print(String.format("任务 %s 下载异常，状态置为0, 异常信息： [%s]", this, e.toString()));
            } finally {
                if (status != STATUS_DONE) {
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

    public static void main(String[] args) throws IOException {
        String source = "changelog.file";
        String[] paths = {"http://localhost:8080/docs/" + source, "http://localhost:8081/docs/" + source, "http://localhost:8081/examples/" + source, };
        String downloadPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        UrlFileSegmentDownload load = new UrlFileSegmentDownload(3 * 1024, 5, 200, true);
        long before = System.currentTimeMillis();
        String download = load.download(paths, downloadPath);
        long endTime = System.currentTimeMillis();
        System.out.println("下载共耗时: " + (endTime - before) + "ms");
        System.out.println("下载完成后文件大小： " + new File(download).length() + "byte");
        System.out.println("下载后MD5：" + MD5Util.getFileMD5String(new File(download)));
    }
}
