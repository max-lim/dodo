package org.dodo.provider.invoker;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jdk生成service服务工厂
 * @author maxlim
 *
 */
public class JDKServiceInvoker implements ServiceInvoker {
	private final Map<String, Object> services = new ConcurrentHashMap<>();
	private final Map<Class, Map<String, Method>> methodsCached = new ConcurrentHashMap<>();

	@Override
	public Object invoke(String interfaceName, String methodName, Object[] args) throws Exception {
		Object instance = services.get(interfaceName);
		if (instance == null) {
			throw new IllegalArgumentException("instance of " + interfaceName + " is not defined");
		}
		Method method = methodsCached.get(instance.getClass()).get(methodName);
		if (method == null) {
			throw new IllegalArgumentException("method " + methodName + " is not found");
		}
		return method.invoke(instance, args);
	}

	@Override
	public void register(String interfaceName, Object instance) {
		services.putIfAbsent(interfaceName, instance);
		initMethods(instance);
	}
	/**
	 * 初始化Methods缓存
	 * @param instance
	 */
	private void initMethods(Object instance) {
		methodsCached.putIfAbsent(instance.getClass(), new ConcurrentHashMap<>());
		Map<String, Method> classMethods = methodsCached.get(instance.getClass());
		Method[] methods = instance.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			classMethods.put(method.getName(), method);
		}
	}
}
