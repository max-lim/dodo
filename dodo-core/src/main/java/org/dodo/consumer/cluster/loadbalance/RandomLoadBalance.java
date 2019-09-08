package org.dodo.consumer.cluster.loadbalance;

import org.dodo.common.utils.RandomUtils;
import org.dodo.consumer.invoker.InvokerRequest;

import java.util.List;

/**
 * 随机负载均衡
 * @author maxlim
 *
 */
public class RandomLoadBalance extends AbstractLoadBalance {

	@Override
	public String doSelect(InvokerRequest request, List<String> nodes) {
		return nodes.get(RandomUtils.nextInt(0, nodes.size()));
	}
}
