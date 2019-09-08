package org.dodo.common.utils;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class ClassUtil {

    public static ClassLoader findClassLoader(Class<?> clazz) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = clazz.getClassLoader();
        }
        return classLoader;
    }
    
    public static Enumeration<URL> findResourcesByClassLoader(String fileName, ClassLoader classLoader) throws IOException {
    	Enumeration<URL> urls;
    	if (classLoader != null) {
            urls = classLoader.getResources(fileName);
        } else {
            urls = ClassLoader.getSystemResources(fileName);
        }
    	return urls;
    }

    public static <T> T conversionStringType(String value, Class<T> returnType) {
        if (returnType == String.class) {
            return (T) value;
        } else if(returnType == int.class || returnType == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (returnType == long.class || returnType == Long.class) {
            return (T) Long.valueOf(value);
        } else if (returnType == boolean.class || returnType == Boolean.class) {
            return (T) Boolean.valueOf(value);
        } else if (returnType == byte.class || returnType == Byte.class) {
            return (T) Byte.valueOf(value);
        } else if (returnType == char.class || returnType == Character.class) {
            return (T) Character.valueOf(value.charAt(0));
        } else if (returnType == short.class || returnType == Short.class) {
            return (T) Short.valueOf(value);
        } else if (returnType == double.class || returnType == Double.class) {
            return (T) Double.valueOf(value);
        } else if (returnType == float.class || returnType == Float.class) {
            return (T) Float.valueOf(value);
        }
        return JSON.parseObject(value, returnType);
    }
}
