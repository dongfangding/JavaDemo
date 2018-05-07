package jdk.juc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author DDf on 2018/5/7
 */
public class MoreTaskSummaryTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        List<String> list = new ArrayList<>();
        // 提取出一个共用实例，来保证同步代码块的功能

        Object object = new Object();
        for (int i = 1; i <= 10; i++) {
            // 每个线程单独操作一个任务，共同资源和不同操作的混合体
            FileTask task = new FileTask(latch,"D:" + File.separator + "filelist" + File.separator + "file" + i + ".txt", list, object);
            new Thread(task, "线程" + i).start();
        }
        latch.await();
        list.forEach(System.out::println);
    }
}

class FileTask implements Runnable {
    private String filePath;
    private List<String> content;
    private CountDownLatch latch;
    private Object object;

    public FileTask(CountDownLatch latch, String filePath, List<String> content, Object object) {
        this.latch = latch;
        this.filePath = filePath;
        this.content = content;
        this.object = object;
    }

    @Override
    public void run() {
        synchronized (object) {
            try {
                File file = new File(filePath);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String temp;
                while ((temp = br.readLine()) != null) {
                    content.add(temp);
                }
                System.out.println(Thread.currentThread().getName() + "当前content: " + content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }
}
