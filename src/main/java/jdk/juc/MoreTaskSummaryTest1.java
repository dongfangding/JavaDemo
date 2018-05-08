package jdk.juc;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author DDf on 2018/5/7
 */
public class MoreTaskSummaryTest1 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // CountDownLatch latch = new CountDownLatch(10);
        List<String> list = new ArrayList<>();
        long before = System.currentTimeMillis();
        for (int i = 1; i <= 10; i++) {
            // FileTask1 task = new FileTask1(latch,"D:" + File.separator + "filelist" + File.separator + "file" + i + ".txt");
            FileTask1 task = new FileTask1("D:" + File.separator + "filelist" + File.separator + "file" + i + ".txt");
            FutureTask<String> result = new FutureTask<>(() -> task.setContent());
            new Thread(result, "线程_" + i).start();
            // get自带闭锁功能
            list.add(result.get());
        }
        // latch.await();
        long after = System.currentTimeMillis();
        System.out.println("一共耗时:" + (after - before));
        System.out.println("list size :" + list.size());
    }
}


class FileTask1 {
    private String filePath;
    private CountDownLatch latch;

    public FileTask1(String filePath) {
        this.filePath = filePath;
    }

    public FileTask1(CountDownLatch latch, String filePath) {
        this.latch = latch;
        this.filePath = filePath;
    }

    public String setContent() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            StringBuffer sbl = new StringBuffer();
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
                sbl.append(temp);
            }
            return sbl.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
        return null;
    }
}
