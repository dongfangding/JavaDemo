package jdk.juc;


import org.junit.jupiter.api.Test;

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
        long before = System.currentTimeMillis();
        // 提取出一个共用实例，来保证同步代码块的功能
        Object object = new Object();
        for (int i = 1; i <= 10; i++) {
            // 每个线程单独操作一个任务，共同资源和不同操作的混合体
            FileTask task = new FileTask(latch,"D:" + File.separator + "filelist" + File.separator + "file" + i + ".txt", list, object);
            new Thread(task, "线程" + i).start();
        }
        latch.await();
        long after = System.currentTimeMillis();
        System.out.println("一共耗时:" + (after - before));
        System.out.println("list size :" + list.size());
        // list.forEach(System.out::println);
    }
    
    @Test
    public void test() {
        List<String> list = new ArrayList<>();
    	long before = System.currentTimeMillis();
    	for (int i = 1; i <= 10; i++) {
    		File file = new File("D:" + File.separator + "filelist" + File.separator + "file" + i + ".txt");
    		if (!file.exists()) {
    		    continue;
            }
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String temp;
            try {
				while ((temp = br.readLine()) != null) {
                    list.add(temp);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    	long after = System.currentTimeMillis();
        System.out.println("一共耗时:" + (after - before));
        System.out.println("list size: " + list.size());
    }
}

class FileTask implements  Runnable {
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
        // notSyncObject();
        // syncObjectRun();
        syncAddRun();
    }

    private void notSyncObject() {
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
            String currFileStr = "";
            while ((temp = br.readLine()) != null) {
                content.add(temp);
                currFileStr += temp;
            }
            Thread.sleep(6000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void syncAddRun() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
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
                synchronized (object) {
                    content.add(temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }


    private void syncObjectRun() {
        synchronized (object) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    return;
                }
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }
}
