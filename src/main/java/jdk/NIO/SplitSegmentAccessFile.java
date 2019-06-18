package jdk.NIO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分段下载文件
 *
 * @author dongfang.ding
 * @date 2019/6/18 14:00
 */
public class SplitSegmentAccessFile {

    public static void main(String[] args) {
        String filePath = System.getProperty("user.dir") + "/src/main/resources/SplitSegmentAccessFile.txt";
        SegmentDownloadTask task = new SegmentDownloadTask(5, filePath);
        String execute = task.execute();
        System.out.println("下载后的文件名为 = " + execute);
    }
}


/**
 * 构建一个对文件进行分段多线程下载的任务类
 */
class SegmentDownloadTask {

    /** 分段数量 */
    private int segment;

    /** 用以协调线程下载 */
    private CountDownLatch countDownLatch;

    /** 下载源文件 */
    private String fileName;

    /** 线程池 */
    private ExecutorService executorService;

    SegmentDownloadTask(int segment, String fileName) {
        this.segment = segment;
        this.countDownLatch = new CountDownLatch(segment);
        this.fileName = fileName;
        executorService = Executors.newFixedThreadPool(segment * 2);
    }

    SegmentDownloadTask(int segment, String fileName, ExecutorService executorService) {
        this.segment = segment;
        this.countDownLatch = new CountDownLatch(segment);
        this.fileName = fileName;
        this.executorService = executorService;
    }

    public String execute() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rws")) {
            Path path = Paths.get(fileName);
            String fileName = path.getFileName().toString();
            // 设置备份文件名,真实场景，最好加个时间戳
            String backup = path.getParent() + File.separator + fileName.substring(0, fileName.lastIndexOf(".")) + "_bak" + fileName.substring(fileName.lastIndexOf("."));
            File file = new File(backup);
            if (file.exists()) {
                file.delete();
            }
            RandomAccessFile backupAccessFile = new RandomAccessFile(backup, "rws");

            long length = randomAccessFile.length();
            // 计算根据分段之后每段需要读取字节的平均数
            long average = length / segment;
            for (int i = 0; i < segment; i++) {
                int finalI = i;
                executorService.execute(() -> {
                    try {
                        // 设置当前线程需要读取源文件的起始点
                        randomAccessFile.seek(finalI * average);
                        // 设置当前线程需要读取多少字节
                        byte[] buf = new byte[(int) average];
                        randomAccessFile.read(buf);
                        // 设置当前线程需要从哪个偏移量开始写入字节，需要注意的是，如果后面的线程先写了，那么前面会用NULL代替，直到后来
                        // 真的处理这部分字节的线程开始处理时会再把null覆盖
                        backupAccessFile.seek(finalI * average);
                        backupAccessFile.write(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            // 锁等待，所有分段任务完成之后再继续主线程
            countDownLatch.await();
            // 关闭线程池，如果是web应用，不需要关闭
            executorService.shutdown();
            return file.getAbsolutePath();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}