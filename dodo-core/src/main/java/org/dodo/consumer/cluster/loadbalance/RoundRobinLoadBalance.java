package org.dodo.consumer.cluster.loadbalance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dodo.common.utils.RoundRobinLinked;
import org.dodo.consumer.invoker.InvokerRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 轮询负载均衡
 * @author maxlim
 *
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {
	protected ConcurrentHashMap<String, RoundRobinLinked> linked = new ConcurrentHashMap<>();
	@Override
	public String doSelect(InvokerRequest request, List<String> nodes) {
		RoundRobinLinked roundRobinLinked = linked.get(request.getClassName());
		if( ! linked.containsKey(roundRobinLinked)) {
			linked.putIfAbsent(request.getClassName(), RoundRobinLinked.build(nodes));
			roundRobinLinked = linked.get(request.getClassName());
		}
		return roundRobinLinked.selectKey();
	}

	@Override
	public void onRefresh(String key, String node, RefreshConst refreshConst) {
		RoundRobinLinked roundRobinLinked = linked.get(key);
		if(roundRobinLinked != null) {
			if(refreshConst == RefreshConst.ADD) roundRobinLinked.addNode(node);
			else roundRobinLinked.removeNode(node);
		}
		else {
			if(refreshConst == RefreshConst.ADD) {
				RoundRobinLinked old = linked.putIfAbsent(key, new RoundRobinLinked().addNode(node));
				if(old != null) old.addNode(node);
			}
		}
	}

	@Override
    public void setWeight(String weights) {
		JSONObject json = JSON.parseObject(weights);
		String service = json.getString("service");
		Map<String, Integer> nodesWithWeights = json.getObject("weights", Map.class);

		//根据值倒排
		Map<String, Integer> nodesWithWeightsOrdered = new LinkedHashMap<>(nodesWithWeights.size());
		nodesWithWeights.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEachOrdered(e -> nodesWithWeightsOrdered.put(e.getKey(), e.getValue()));

		RoundRobinLinked roundRobinLinked = linked.get(service);
		if(roundRobinLinked != null) roundRobinLinked.rebuild(nodesWithWeightsOrdered);
		else linked.put(service, RoundRobinLinked.build(nodesWithWeightsOrdered));
    }
}
