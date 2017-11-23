package main.java.jdk.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntityCopy {
	private static final String dateFormat = "yyyy-MM-dd";
	private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

	public static void main(String[] args) {
		
	}
	
	/**
	 * 根据给定的字符数组数据在两个对象之间拷贝数据
	 * 日期仅支持yyyy-MM-dd  和yyyy-MM-dd HH:mm:ss
	 * @param source 源对象
	 * @param taget 目标对象
	 * @param copyProperties 指定需要拷贝的字段数组
	 */
	public static void entityCopy(Object source, Object taget, String[] copyProperties) {
		try {
			Class<? extends Object> sourceClazz = source.getClass();
			Class<? extends Object> targetClazz = taget.getClass();
			if(copyProperties != null && copyProperties.length > 0) {
				String updaCaseField = "";
				for (String prop : copyProperties) {
					Field field = sourceClazz.getDeclaredField(prop);
					Class<?> fieldClazz = field.getType();
					updaCaseField = prop.substring(0, 1).toUpperCase() + prop.substring(1);
					Method getMethod = sourceClazz.getDeclaredMethod("get" + updaCaseField, fieldClazz);
					Object value = getMethod.invoke(sourceClazz);
					Method setMethod = targetClazz.getDeclaredMethod("set" + updaCaseField, fieldClazz);
					if (value != null) {
						setMethod.invoke(targetClazz, value);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("entity copy error, " + e);
		}
	}
	
	public static Object parseValue(Type fieldType, String value) {
		Object objValue = value;
		if (!fieldType.equals(String.class)) {
			if (fieldType.equals(Integer.class)) {
				objValue = Integer.parseInt(value);
			} else if (fieldType.equals(Byte.class)) {
				objValue = Byte.parseByte(value);
			} else if (fieldType.equals(Long.class)) {
				objValue = Long.parseLong(value);
			} else if (fieldType.equals(Short.class)) {
				objValue = Short.parseShort(value);
			} else if (fieldType.equals(Float.class)) {
				objValue = Float.parseFloat(value);
			} else if (fieldType.equals(Double.class)) {
				objValue = Double.parseDouble(value);
			} else if (fieldType.equals(BigInteger.class)) {
				objValue = new BigInteger(value);
			} else if (fieldType.equals(BigDecimal.class)) {
				objValue = new BigDecimal(value);
			} else if (fieldType.equals(Date.class)) {
				objValue = string2Date(value);
			}
		}
		return objValue;
	}
	
	public static Date string2Date(String str) {
		Date aDate = null;
		try {
			if (str.contains(":")) {
				aDate = new SimpleDateFormat(timeFormat).parse(str);
			} else {
				aDate = new SimpleDateFormat(dateFormat).parse(str);
			}
		} catch (ParseException e) {
			
		}
		return aDate;
	}
}
