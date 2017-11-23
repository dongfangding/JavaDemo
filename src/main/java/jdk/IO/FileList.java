package main.java.jdk.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileList {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		System.out.println(map.get("12"));
	}
	
	public static void fileList() {
		String path = "C:" + File.separator + "IN" + File.separator;
		if(!new File(path).exists()) {
			new File(path).mkdirs();
		}
		File[] file = new File(path).listFiles();
		/*if(file.length > 0) {
			for(File fileName : file) {
				System.out.println(fileName.getName());
			}
		}*/
		List<String> fileNameList = new ArrayList<String>();
		for (int i = 0; i < file.length; i++) {
	    	if (file[i].isFile()) {
	    		fileNameList.add(file[i].toString());
	    	}
    	}
		if(fileNameList != null && fileNameList.size() > 0) {
			for(String fileName : fileNameList) {
				System.out.println(fileName);
				System.out.println(new File(fileName).getName());
			}
		}
	}
	
	public static void fileName() {
		String path = "C:/AEC_DATA/data/fedEx/openShipment/783675009556/OUTBOUND_LABEL.783675009556_0.zpl";
		if(!new File(path).exists()) {
			new File(path).mkdirs();
		}
		System.out.println(new File(path).getParentFile());
	}
	
	public static void fileList2() {
		String path = "C:/AEC_DATA/data/fedEx/openShipment";
		if(!new File(path).exists()) {
			new File(path).mkdirs();
		}
		String[] list = new File(path).list();
		for(String l : list) {
			System.out.println(l);
		}
		File[] fileList = new File(path).listFiles();
		for(File f : fileList) {
			if(f.isDirectory())
			System.out.println(f.getName());
		}
	}

}
