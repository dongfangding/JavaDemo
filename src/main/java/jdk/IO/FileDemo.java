package jdk.IO;

import java.io.File;
import java.io.IOException;


public class FileDemo {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File file = new File("c:\\file");
        File file1 = new File(file, "fileOne.txt");
        File file2 = new File(file, "fileTwo.java");

        if (!file.exists()) {
            file.mkdir();
        }
        if (!file1.exists()) {
            file1.createNewFile();
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }
        System.out.println("fdass");

        System.out.println("f1's AbsolutePath=  " + file1.getAbsolutePath());
        System.out.println("f1 Canread=" + file1.canRead());
        System.out.println("f1's len= " + file1.length());


        System.out.println(file1.toString());
        int b;
        try {
            System.out.println("please Input:");
            while ((b = System.in.read()) != -1) {
                System.out.print((char) b);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

}
