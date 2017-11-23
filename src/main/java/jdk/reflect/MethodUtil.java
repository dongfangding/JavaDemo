package main.java.jdk.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodUtil {
	private static Map<String, Set<String>> methodNameCache = new ConcurrentHashMap<String, Set<String>>();
	private static Map<String, Set<Method>> methodCache = new ConcurrentHashMap<String, Set<Method>>();
	private static Map<String, Map<String, Method>> methodSetCache = new ConcurrentHashMap<String, Map<String, Method>>();
	private static Map<String, Map<String, Method>> methodGetCache = new ConcurrentHashMap<String, Map<String, Method>>();
	private static Logger logger = LoggerFactory.getLogger(MethodUtil.class);

	/**
	 * 获取某个对象的所有方法的名称.
	 * 
	 * @param obj
	 * @return
	 */
	public static Set<String> getMethodsName(Object obj) {
		Class<? extends Object> clazz = obj.getClass();
		return getMethodsName(clazz);
	}

	/**
	 * 获取某个类的所有方法的名称.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Set<String> getMethodsName(Class<? extends Object> clazz) {
		Set<String> methodSet = null;
		methodSet = methodNameCache.get(clazz.getSimpleName());
		if (methodSet == null) {
			methodSet = new HashSet<String>();
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				methodSet.add(method.getName());
			}
			methodNameCache.put(clazz.getSimpleName(), methodSet);
		}
		return methodSet;
	}

	/**
	 * 获取某个类的所有方法.
	 * 
	 * @param clazz
	 * @return
	 */
	private static Set<Method> getMethods(Class<? extends Object> clazz) {
		Set<Method> methodSet = null;
		methodSet = methodCache.get(clazz.getSimpleName());
		if (methodSet == null) {
			methodSet = new HashSet<Method>();
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				methodSet.add(method);
			}
			methodCache.put(clazz.getSimpleName(), methodSet);
		}
		return methodSet;
	}

	/**
	 * 设置某个对象的某个属性(调用该对象属性的set方法).
	 * 
	 * @param entity
	 *            对象
	 * @param fieldName
	 *            属性名字
	 * @param paramClass
	 *            参数类型
	 * @param paramValue
	 *            参数的值
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void doSetMethod(Object entity, String fieldName, Class paramClass, Object paramValue) {
		Set<String> methodSet = getMethodsName(entity);
		if (methodSet.contains("set" + StringUtil.capitalize(fieldName))) {
			try {
				Method method = entity.getClass().getMethod("set" + StringUtil.capitalize(fieldName),
						new Class[] { paramClass });
				method.invoke(entity, new Object[] { paramValue });
			} catch (Exception e) {
				logger.error("do set method " + fieldName, e);
			}
		}
	}

	/**
	 * 获取某个对象的某个属性的值(调用该对象属性的get方法).
	 * 
	 * @param entity
	 *            对象
	 * @param fieldName
	 *            属性名
	 * @return
	 */
	public static Object doGetMethod(Object entity, String fieldName) {
		Object ret = null;
		Set<String> methodSet = getMethodsName(entity);
		if (methodSet.contains("get" + StringUtil.capitalize(fieldName))
				|| methodSet.contains(StringUtil.uncapitalize(fieldName))) {
			Method method;
			try {
				method = entity.getClass().getMethod("get" + StringUtil.capitalize(fieldName));
				ret = method.invoke(entity);
			} catch (Exception e) {
				logger.error("do get method " + fieldName, e);
			}
		}
		return ret;
	}

	/**
	 * 获取对象的所有set方法
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Method> getSetMethods(Object obj) {
		Class clazz = obj.getClass();
		Map<String, Method> methodMap = methodSetCache.get(clazz.getSimpleName());
		if (methodMap == null) {
			methodMap = new HashMap<String, Method>();
			Set<Method> methods = getMethods(clazz);
			for (Method method : methods) {
				if (method.getName().startsWith("set")) {
					methodMap.put(StringUtil.uncapitalize(method.getName().substring(3)), method);
				}
			}
			methodSetCache.put(clazz.getSimpleName(), methodMap);
		}
		return methodMap;
	}
	
	/**
	 * 获取对象的所有get方法
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Method> getGetMethods(Object obj) {
		Class clazz = obj.getClass();
		if(obj instanceof Class) {
			clazz = (Class) obj;
		}
		Map<String, Method> methodMap = methodGetCache.get(clazz.getSimpleName());
		if (methodMap == null) {
			methodMap = new HashMap<String, Method>();
			Set<Method> methods = getMethods(clazz);
			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					methodMap.put(StringUtil.uncapitalize(method.getName().substring(3)), method);
				}
			}
			methodGetCache.put(clazz.getSimpleName(), methodMap);
		}
		return methodMap;
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
						Object paramObj = StringUtil.parseValue(fieldClazz, value.toString());
						setMethod.invoke(targetClazz, paramObj);
					}
				}
			}
		} catch (Exception e) {
			logger.error("entity copy error, ", e);
		}
	}
}
