package jdk.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileInputStreamDemo {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        File file = new File("c:\\file");    //create a folder
        File file1 = new File(file, "fileOne.txt"); //create a file
        File file2 = new File(file, "fileTwo.txt"); //create a file

        if (!file.exists()) {
            file.mkdir();   //if folder not exist, create it
        }
        if (!file1.exists()) {
            file1.createNewFile();  //if file not exist , create this .
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }

        System.out.println("f1's AbsolutePath=  " + file1.getAbsolutePath());
        System.out.println("f1 Canread=" + file1.canRead());
        System.out.println("f1's len= " + file1.length());

        System.out.println("Please input infomation into the file:");
        byte buffer[] = new byte[512];
        try {
            FileOutputStream fos = new FileOutputStream("c://file/fileOne.txt");

            int count = 0;
            try {
                count = System.in.read(buffer);
                fos.write(buffer);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        try {
            FileInputStream fis = new FileInputStream("c://file/fileOne.txt");
            FileOutputStream fos2 = new FileOutputStream("c://file/fileTwo.txt");
            try {
                while (fis.read(buffer) != -1) {
                    System.out.print(new String(buffer));
                    fos2.write(buffer);
                }
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

    }

}
