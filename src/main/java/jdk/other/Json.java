package main.java.jdk.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.java.entity.Student;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class Json {

	public static void main(String[] args) {
		String jsonStr = "{\"result\":{\"returncode\":\"0\",\"returnmsg\":\"\",\"invoicetype\":\"3948801001004724400001\",\"invoicetaxno\":\"345678\",\"invoicestatus\":\"0\",\"payername\":\"xxxxx\",\"issudedate\":\"2014-11-27\",\"currencycode\":\"cny\",\"invoiceamount\":\"100\",\"vatfee\":\"6\",\"amountnoamount\":\"94\",\"vatrate\":\"0.06\",\"issuer\":\"xxx\",\"checker\":\"xxx\",\"mailaddress\":\"xxxx\",\"mailreceiver\":\"xxxx\",\"mailreceiverphone\":\"135xxx\"}}";
		int index = 0;
		//Json.strToMap(jsonStr, index);
		//Json.beanToJson(jsonStr);
		strToMap();
	}
	
	/**
	 * json字符串转Map
	 * 因为原字符串有两层嵌套，所以这里用了递归，取出真正的数据逻辑
	 * @author ddf 20150804
	 * @param jsonStr
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public static void strToMap(String jsonStr, int index){
		Map<String, Object> objMap = new HashMap<String, Object>();
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		Iterator<String> iter = jsonObject.keys();
		String key = "";
		index ++;
		while(iter.hasNext()){
			key = iter.next();
			objMap.put(key, jsonObject.get(key));
		}
		for(Map.Entry<String, Object> entry : objMap.entrySet()){
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		if(index == 1){
			strToMap(objMap.get("result").toString(), index);
		}
	}
	
	/**
	 * 对象转换成JSONObject,对象列表转换成JSONArray
	 * @author ddf 20150805
	 * @param jsonStr
	 */
	public static void beanToJson(String jsonStr){
		Student stu = new Student("yichen", "23", "男", "1001", "13162798272");
		Student stu1 = new Student("yichen1", "24", "男", "1002", "18326160171");
		Student stu2 = new Student("yichen2", "25", "男", "1003", "15665330742");
		List<Student> stuList = new ArrayList<Student>();
		JSONObject jsonObject = JSONObject.fromObject(stu);
		System.out.println("-----------------------JSONObject--------------------\n");
		System.out.println("JSONObject:---:" + jsonObject.toString());
		Iterator<String> iter = jsonObject.keys();
		String key = null;
		// 单个对象用JSONObject
		while(iter.hasNext()){
			key = iter.next();
			System.out.println(key + ":" + jsonObject.optString(key));
		}
		System.out.println("\n-----------------------JSONObject--------------------");
		stuList.add(stu);
		stuList.add(stu1);
		stuList.add(stu2);
		System.out.println("\n");
		System.out.println("-----------------------JSONArray--------------------\n");
		// 多个对象用JSONArray，再循环转换成JSONObject
		JSONArray jsonArray = JSONArray.fromObject(stuList);
		for(int i = 0; i < jsonArray.size(); i ++){
			JSONObject jsonObject1 = jsonArray.getJSONObject(i);
			Iterator<String> iter1 = jsonObject1.keys();
			while(iter1.hasNext()){
				key = iter1.next();
				System.out.print(key + ":" + jsonObject1.optString(key));
				System.out.print("\t");
			}
			System.out.println("\n");
		}
		System.out.println("-----------------------JSONArray--------------------");
		
		System.out.println("\n");
		System.out.println("------------Map<String, JSONArray> TO JSONObject----------\n");
		// 将对象作为JSONObject的key， 将整个对象数组作为key
		Map<String, JSONArray> jsonMap = new HashMap<String, JSONArray>();
		jsonMap.put("Student", jsonArray);
		JSONObject mapJson = JSONObject.fromObject(jsonMap);
		System.out.println("对象MAP数组JSON:" + mapJson.toString());
		String stuJsonStr = mapJson.optString("Student");
		JSONArray stuJsonArr = JSONArray.fromObject(stuJsonStr);
		for(int i = 0; i < stuJsonArr.size(); i ++){
			JSONObject stuJson = stuJsonArr.getJSONObject(i);
			for(Object key1 : stuJson.keySet()) {
				System.out.print(key1 + ":" + stuJson.optString(key1.toString()));
				System.out.print("\t");
			}
			System.out.println();
		}
		System.out.println("\n------------Map<String, JSONArray> TO JSONObject----------");
	}
	
	
	@SuppressWarnings("unchecked")
	public static void strToMap() {
		String jsonStr = "{\"parameter\":{\"Shipment\":[{\"shipmentID\":\"EXP_20160603150101\",\"package\":["
				+ "{\"packageID\":\"E_EXP_PACKAGE_10\",\"packageLength\":\"10\",\"packageWidth\":\"11\",\"packageHeight"
				+ "\":\"12\",\"packageVolume\":\"13\",\"packageWeight\":\"14\"},{\"packageID\":\"E_EXP_PACKAGE_100\","
				+ "\"packageLength\":\"101\",\"packageWidth\":\"112\",\"packageHeight\":\"123\",\"packageVolume\":"
				+ "\"134\",\"packageWeight\":\"145\"}]},{\"shipmentID\":\"EXP_20160603150102\",\"package\":"
				+ "[{\"packageID\":\"E_EXP_PACKAGE_200\",\"packageLength\":\"201\",\"packageWidth\":\"212\","
				+ "\"packageHeight\":\"223\",\"packageVolume\":\"234\",\"packageWeight\":\"245\"}]}]}}";
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		JSONObject paramObject = jsonObject.getJSONObject("parameter");
		JSONArray shipJsonArr = paramObject.getJSONArray("Shipment");
		Iterator<JSONObject> shipJsonArrIter = shipJsonArr.iterator();
		String expressNo  = "";
		String key = "";
		while(shipJsonArrIter.hasNext()) {
			JSONObject shipJsonObj = shipJsonArrIter.next();
			expressNo = shipJsonObj.optString("shipmentID");
			System.out.println("Shipment\n\t" + expressNo);
			Iterator<JSONObject> packageJsonArrIter = shipJsonObj.getJSONArray("package").iterator();
			System.out.println("package");
			while(packageJsonArrIter.hasNext()) {
				JSONObject packageJsonObj = packageJsonArrIter.next();
				Iterator<String> packageKey = packageJsonObj.keys();
				while(packageKey.hasNext()) {
					key = packageKey.next();
					System.out.println("\t" + key + ":" + packageJsonObj.optString(key));
				}
				System.out.println();
			}
			System.out.println("----------------------------");
		}
		
	}
}
