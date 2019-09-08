package org.dodo.consumer.invoker;

import org.dodo.common.spi.Spi;

/**
 * 消费者端反射之后发起rpc调用的入口
 * @author maxlim
 *
 */
@Spi("failfast")
public interface Invoker {
	/**
	 * 执行服务调用
	 * @param invokerRequest
	 * @return
	 * @throws Exception
	 */
	public Object invoke(InvokerRequest invokerRequest) throws Exception;
}
