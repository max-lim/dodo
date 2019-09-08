package org.dodo.consumer.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;
import org.dodo.config.ReferenceConfig;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * jdk反射
 * @author maxlim
 *
 */
public class JDKReflectHandler implements InvocationHandler, ReflectHandler {
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return this.invoke(method.getDeclaringClass(), method, args);
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public <T> T proxy(Class<T> target) {
		return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class[] {target}, this); 
	}

	public Object invoke(Class<?> clazz, Method method, Object[] args) throws Exception {
		ReferenceConfig referenceConfig = ConfigManager.instance().getReferenceConfig(clazz.getName());

		InvokerRequest request = new InvokerRequest();
		request.setClazz(clazz);
		request.setMethod(method);
		request.setArgs(args);
		request.setReferenceConfig(referenceConfig);

		String cluster = Optional.ofNullable(referenceConfig.getCluster()).orElse(ConfigManager.instance().getConsumerConfig().getCluster());
		Invoker invoker = SpiLoader.getExtensionHolder(Invoker.class).get(cluster);
		return invoker.invoke(request);
	}
}
