package main.java.jdk.other;

import java.util.HashSet;
import java.util.Set;

public class NumIsSix {
	public static void main(String []args){
		int ge = 0; 
		int shi = 0;
		int bai = 0; 
		int qian = 0;
		int total = 0;
		for(int i = 1000; i < 10000; i ++){
			ge = i % 1000 % 100 % 10; // 这个代表个位数
			shi = i % 100 / 10;    // 这个代表十位数
			bai = i % 1000 / 100; // 这个代表百位数
			qian = i / 1000;   //这个代表千位数
			
			//这个是用来去重的
			Set<Integer> set = new HashSet<Integer>();
			set.add(ge);
			set.add(shi);
			set.add(bai);
			set.add(qian);
			//装入四个数字，如果长度为4说明没有重复数据
			if(set.size() == 4 && (ge + shi + bai + qian == 6)){
				total ++;
				System.out.println(total + "：" + i);
			}
		}
	}
}
