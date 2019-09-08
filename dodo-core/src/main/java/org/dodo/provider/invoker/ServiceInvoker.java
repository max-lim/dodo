package org.dodo.provider.invoker;

import org.dodo.common.spi.Spi;

/**
 * service类工厂
 * @author maxlim
 *
 */
@Spi("javassist")
public interface ServiceInvoker {
	/**
	 * 获取接口对应的实现类
	 * @param interfaceName
	 * @param methodName
	 * @return
	 */
	public Object invoke(String interfaceName, String methodName, Object[] args) throws Exception;

	/**
	 * 执行添加接口的实现类时的逻辑
	 * @param interfaceName
	 * @param instance
	 */
	public void register(String interfaceName, Object instance) throws Exception;
}
