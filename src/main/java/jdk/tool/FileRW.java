package jdk.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileRW {

    /**
     * 文件读取
     *
     * @param path
     * @return
     */
    public static String fileRead(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String rtStr = "";
        try {
            String tempString = "";
            reader = new BufferedReader(new FileReader(file));
            while ((tempString = reader.readLine()) != null) {
                rtStr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return rtStr;
    }

    /**
     * 写入文件
     *
     * @param content
     * @param path
     */
    public static void fileWrite(String content, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public static void fileWrite(String content,String path)
    		   throws IOException {
		  File file = new File(path);
		  file.delete();
		  file.createNewFile();
		  BufferedWriter writer = new BufferedWriter(
				  new OutputStreamWriter(
						  new FileOutputStream(file), "UTF-8"
				    )
				 );
		  writer.write(content);
		  writer.close();
	}*/

    //读取目录文件
    public static List<String> findDirectoryFile(String path) {
        List<String> rtList = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                rtList.add(tempList[i].toString());
            }
	    	/*if (tempList[i].isDirectory()) {
	    		System.out.println("文件夹："+tempList[i]);
	    	}*/
        }
        return rtList;
    }

    /**
     * 删除单个文件
     *
     * @param path 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
    
	/*public static void main(String[] args){
		FileRW f=new FileRW();
		//f.fileWrite("123","C:\\git\\123.txt");
		//System.out.println(f.findDirectoryFile("E:\\duoli").toString());
	}*/
}
