package org.dodo.consumer.cluster;

import org.dodo.consumer.cluster.loadbalance.LoadBalance;
import org.dodo.common.spi.SpiLoader;
import org.dodo.common.utils.StringUtils;
import org.dodo.config.ConfigManager;
import org.dodo.config.MethodConfig;
import org.dodo.config.ReferenceConfig;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.register.URL;
import org.dodo.rpc.Node;

import java.util.Optional;

/**
 * 集群调用抽象实现
 * @author maxlim
 *
 */
public abstract class AbstractClusterInvoker implements Invoker {
	/**
	 * 执行服务调用选择的ServerNode
	 * @param invokerRequest
	 * @param serverNode
	 * @return
	 */
	public abstract Object invoke(InvokerRequest invokerRequest, Node serverNode) throws Exception;

	public Object invoke(InvokerRequest invokerRequest) throws Exception {
		Object result = invoke(invokerRequest, selectServerNode(invokerRequest));
		return result;
	}
	/**
	 * 选择一个节点
	 * @param invokerRequest
	 * @return
	 */
	public Node selectServerNode(InvokerRequest invokerRequest) throws Exception {
		ReferenceConfig referenceConfig = invokerRequest.getReferenceConfig();
		String loadBalanceName = Optional.ofNullable(referenceConfig.getLoadBalance()).orElse(ConfigManager.instance().getConsumerConfig().getLoadBalance());
		LoadBalance loadBalance = SpiLoader.getExtensionHolder(LoadBalance.class).get(loadBalanceName);
		return loadBalance.select(invokerRequest);
	}
	/**
	 * 是否异步调用
	 * @param invokerRequest
	 * @return
	 */
	public boolean isAsync(InvokerRequest invokerRequest) {
		ReferenceConfig referenceConfig = invokerRequest.getReferenceConfig();
		MethodConfig methodConfig = referenceConfig.getMethodConfig(invokerRequest.getMethodName());
		return methodConfig != null && methodConfig.isAsync();
	}
	
	/**
	 * 调用超时时间
	 * @param invokerRequest
	 * @return
	 */
	public long timeout(InvokerRequest invokerRequest, Node node) {
		URL url = node.getURL(invokerRequest.getClassName());
		int timeout = url != null && url.getParams() != null ? Integer.valueOf(url.getParams().get("timeout")): 0;
		if (timeout == 0) {
			ReferenceConfig referenceConfig = invokerRequest.getReferenceConfig();
			timeout = referenceConfig.getTimeout();
		}
		return timeout <=0 ? 1500 : timeout;
	}
	
	/**
	 * 调用尝试次数
	 * @param invokerRequest
	 * @return
	 */
	public int retry(InvokerRequest invokerRequest, Node node) {
		URL url = node.getURL(invokerRequest.getClassName());
		int retry = url != null && url.getParams() != null ? Integer.valueOf(url.getParams().get("retry")): 0;
		if(retry == 0) {
			ReferenceConfig referenceConfig = invokerRequest.getReferenceConfig();
			retry = referenceConfig.getRetry();
		}
		return retry <= 0 ? 1 : retry;
	}
}
