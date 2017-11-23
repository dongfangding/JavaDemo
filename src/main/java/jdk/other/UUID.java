package main.java.jdk.other;

import java.util.Random;

public class UUID {
	public static void main(String []args) {
		System.out.println(UUID.getUUIDByRules());
		System.out.println(java.util.UUID.randomUUID().toString().replace("-", ""));
		System.out.println(UUID.getUUID());
		System.out.println((int)(Math.random()*32));
	}
	public static String getUUIDByRules(){    
		String rules = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		int rpoint = 0;    
		StringBuffer generateRandStr = new StringBuffer();    
		Random rand = new Random();    
		int length = 32;    
		for(int i=0;i<length;i++)    
		{    
		    if(rules!=null){    
		        rpoint = rules.length();    
		        int randNum = rand.nextInt(rpoint);    
		        generateRandStr.append(rules.substring(randNum,randNum+1));    
		    }    
		}    
		return generateRandStr+"";    
	}  
	
	public static String getUUID(){
		String[] chars="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
		String uuids = ""; 
		String  uuid="";
		for (int i=0;i<32;i++){
			uuids=chars[(int) (Math.random()*chars.length)];
			uuid+=uuids;
		}
		return uuid;
	}
}
