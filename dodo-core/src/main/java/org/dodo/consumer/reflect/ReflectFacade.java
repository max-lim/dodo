package org.dodo.consumer.reflect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;

/**
 * 反射调用门面
 * @author maxlim
 *
 */
public class ReflectFacade {
	private static class Single {
		private static final ReflectFacade INSTANCE = new ReflectFacade();
	}
	public static ReflectFacade instance() {
		return Single.INSTANCE;
	}
	private final ReflectHandler reflectHandler;
	private final Map<Class<?>, Object> proxiesCached = new ConcurrentHashMap<>();
	private ReflectFacade() {
		String reflect = ConfigManager.instance().getConsumerConfig() == null ? "jdk" : ConfigManager.instance().getConsumerConfig().getReflect();
		reflectHandler = SpiLoader.getExtensionHolder(ReflectHandler.class).get(reflect);
	}
	
	@SuppressWarnings("unchecked")
	public <T>T reflect(Class<T> clazz) throws Exception {
		T proxy = (T) proxiesCached.get(clazz);
		if(proxy == null) {
			synchronized (proxiesCached) {
				proxy = (T) proxiesCached.get(clazz);
				if(proxy == null) {
					proxiesCached.putIfAbsent(clazz, reflectHandler.proxy(clazz));
					proxy = (T) proxiesCached.get(clazz);
				}
			}
		}
		return proxy;
	}
}
