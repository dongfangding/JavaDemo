package jdk.other;

public class Data {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        long time = 1439877702073L;
        long time1 = System.currentTimeMillis();
        long a = (time1 - time) / 1000 / 60;
        System.out.println("等待");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(a);
    }
}
