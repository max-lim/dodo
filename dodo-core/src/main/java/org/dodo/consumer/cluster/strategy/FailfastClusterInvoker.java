package org.dodo.consumer.cluster.strategy;

import org.dodo.common.spi.Wrapper;
import org.dodo.consumer.cluster.AbstractClusterInvoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Node;

/**
 * 快速失效集群调用方式
 * @author maxlim
 *
 */
@Wrapper("wrapper")
public class FailfastClusterInvoker extends AbstractClusterInvoker {
	
	@Override
	public Object invoke(InvokerRequest invokerRequest, Node serverNode) throws Exception {
		boolean async = this.isAsync(invokerRequest);
		long timeout = this.timeout(invokerRequest, serverNode);
		return serverNode.send(invokerRequest, timeout, async);
	}

}
