package org.dodo.common.spi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import org.dodo.common.utils.ClassUtil;
import org.dodo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * spi加载实现
 * @author maxlim
 *
 */
public class SpiLoader {
    private static final Logger logger = LoggerFactory.getLogger(SpiLoader.class);
    private static final ConcurrentMap<Class<?>, ExtensionHolder<?>> EXTENSION_HOLDERS = new ConcurrentHashMap<>();
    //框架内部扩展类
    private static final String SERVICES_INTERNAL_DIRECTORY = "META-INF/dodo/internal/";
    //对外开放的配置目录，外部扩展不可以覆盖内部的实现
    private static final String SERVICES_DIRECTORY = "META-INF/dodo/";
    //单例实例
    @Deprecated
    private static final ConcurrentHashMap<Class<?>, Object> SINGLE_OBJECTS = new ConcurrentHashMap<>();

    /**
     * 对外开放的配置目录META-INF/dodo/，外部扩展不可以覆盖内部的实现
     * 默认包装装饰器
     * @param <E>
     * @param type
     * @return
     */
	@SuppressWarnings("unchecked")
	public static <E> ExtensionHolder<E> getExtensionHolder(Class<E> type) {
        if ( ! type.isInterface()) {
            throw new IllegalArgumentException("Class type must be an interface!");
        }
        if( ! type.isAnnotationPresent(Spi.class)) {
            throw new IllegalArgumentException("Class type have to add annotation Spi!");
        }
        
        ExtensionHolder<E> holder = null;
        holder = (ExtensionHolder<E>) EXTENSION_HOLDERS.get(type);
        if (holder == null) {
            synchronized (SpiLoader.class) {
                holder = (ExtensionHolder<E>) EXTENSION_HOLDERS.get(type);
                if (holder == null) {
                    EXTENSION_HOLDERS.putIfAbsent(type, loadSpiClass(type));
                    holder = (ExtensionHolder<E>) EXTENSION_HOLDERS.get(type);
                }
            }
        }
        return holder;
    }

    private static <E> ExtensionHolder<E> loadSpiClass(Class<E> type) {
    	ExtensionHolder<E> extensionHolder = new ExtensionHolder<>();
        loadConfigFiles(extensionHolder, SERVICES_INTERNAL_DIRECTORY, type);
        loadConfigFiles(extensionHolder, SERVICES_DIRECTORY, type);
        holdDefaultName(extensionHolder, type);
        decorateByWrapperAnnotation(extensionHolder, type);
        return extensionHolder;
    }
    
    private static <E> void loadConfigFiles(ExtensionHolder<E> extensionHolder, String dir, Class<E> type) {
        String fileName = dir + type.getName();
        try {
            ClassLoader classLoader = ClassUtil.findClassLoader(SpiLoader.class);
            Enumeration<URL> urls = ClassUtil.findResourcesByClassLoader(fileName, classLoader);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadConfigFile(extensionHolder, type, classLoader, url);
                }
            }
        } catch (Throwable t) {
            logger.error("load "+type.getName()+" configfile fail", t);
        }
    }

    private static <E> void loadConfigFile(ExtensionHolder<E> extensionHolder, Class<E> type, ClassLoader classLoader, URL url) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {
                        int idx = line.indexOf('#');
                        if (idx >= 0) {
                        	continue;
                        }
                        try {
                        	idx = line.indexOf('=');
                            if (idx > 0) {
                                String name = line.substring(0, idx).trim();
                                String value = line.substring(idx + 1).trim();
                                if (value.length() > 0) {
                                	loadClass(extensionHolder, type, Class.forName(value, true, classLoader), name);
                                }
                            }
                        } catch (Throwable t) {
                            throw new IllegalStateException("load "+line +" fail", t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("load "+ url +" fail", t);
        }
    }

    @SuppressWarnings("unchecked")
	private static <E> void loadClass(ExtensionHolder<E> extensionHolder, Class<E> type, Class<?> clazz, String name) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        E instance = null;
        if(type.isAnnotationPresent(Single.class)) {
            instance = (E) SINGLE_OBJECTS.get(clazz);
        }
        if(instance != null) {
            extensionHolder.name2Object.put(name, instance);
        }
	    else {
            instance = extensionHolder.get(name);
            if(instance == null) {
                instance = (E) clazz.newInstance();
                inject(type, instance);
                extensionHolder.name2Object.put(name, instance);
            }
            if(type.isAnnotationPresent(Single.class)) {
                SINGLE_OBJECTS.putIfAbsent(clazz, instance);
            }
        }
    }

    /**
     * 注入带有Resource注解的属性或反射spi接口类
     * @param <E>
     * @param type
     * @param instance
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static <E> void inject(Class<E> type, E instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	for(Method method: instance.getClass().getDeclaredMethods()) {
    		if(method.getName().startsWith("set") 
    				&& method.getParameterCount() == 1 
    				&& Modifier.isPublic(method.getModifiers())) {
    			Class<?> paramType = method.getParameterTypes()[0];
    			if(paramType.isAnnotationPresent(Spi.class) && paramType != type) {//type相等是decorator
    				Object param = SpiLoader.getExtensionHolder(paramType).get();
    				method.setAccessible(true);
    				method.invoke(instance, param);
    			}
    		}
    	}
    	for(Field field: instance.getClass().getDeclaredFields()) {
    		if(field.isAnnotationPresent(Resource.class) && field.getType() != type) {
    			Class<?> paramType = field.getType();
    			if(paramType.isAnnotationPresent(Spi.class)) {
    				Object param = SpiLoader.getExtensionHolder(paramType).get();
    				field.setAccessible(true);
    				field.set(instance, param);
    			}
    		}
    	}
    }

    private static <E> void holdDefaultName(ExtensionHolder<E> extensionHolder, Class<E> type) {
        Spi spiAnnot = type.getAnnotation(Spi.class);
        if(StringUtils.isNotEmpty(spiAnnot.value())) {
        	extensionHolder.defaultName = spiAnnot.value();
        }
    }
    
    /**
     * 原始扩展类初始化之后，再判断是否有wrapper注解，进行装饰器包装
     * @param <E>
     * @param extensionHolder
     * @param type
     */
    @SuppressWarnings("unchecked")
	private static <E> void decorateByWrapperAnnotation(ExtensionHolder<E> extensionHolder, Class<E> type) {
    	try {
    		for(String name: extensionHolder.name2Object.keySet()) {
        		E instance = extensionHolder.name2Object.get(name);
        		if(instance.getClass().isAnnotationPresent(Wrapper.class)) {
        			Wrapper wrapperAnnotation = instance.getClass().getAnnotation(Wrapper.class);
        			E wrapperInstance = extensionHolder.get(wrapperAnnotation.value());
        			wrapperInstance = (E)(wrapperInstance.getClass().newInstance());
                    wrapperInstance = injectForWrapper(type, wrapperInstance, instance);
        			extensionHolder.name2Wrapper.put(name, wrapperInstance);
        		}
        	}
    	}
    	catch (Exception e) {
            throw new IllegalStateException(type.getName() + " decorate by @Wrapper annotation fail", e);
		}
    }
    /**
     * 为装饰器注入属性
     * @param <E>
     * @param type
     * @param wrapperInstance
     * @param instance
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static <E> E injectForWrapper(Class<E> type, E wrapperInstance, E instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	for(Method method: wrapperInstance.getClass().getDeclaredMethods()) {
    		if(method.getName().startsWith("set")
    				&& method.getParameterCount() == 1 
    				&& Modifier.isPublic(method.getModifiers())) {
    			Class<?> paramType = method.getParameterTypes()[0];
    			if(paramType.isAnnotationPresent(Spi.class) && paramType == type) {//type相等是decorator
    				method.setAccessible(true);
    				if(method.getReturnType() == type) {
                        E returnInstance = (E) method.invoke(wrapperInstance, instance);
                        return returnInstance;
                    }
    				else {
                        method.invoke(wrapperInstance, instance);
                    }
    			}
    		}
    	}
    	for(Field field: wrapperInstance.getClass().getDeclaredFields()) {
    		if(field.isAnnotationPresent(Resource.class) && field.getType() == type) {
    			field.setAccessible(true);
				field.set(wrapperInstance, instance);
    		}
    	}
    	return wrapperInstance;
    }
}
