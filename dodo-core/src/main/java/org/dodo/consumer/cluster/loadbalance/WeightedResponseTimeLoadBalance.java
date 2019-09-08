package org.dodo.consumer.cluster.loadbalance;

import org.dodo.common.thread.NamedThreadFactory;
import org.dodo.common.utils.RoundRobinLinked;
import org.dodo.config.ConfigManager;
import org.dodo.config.ConsumerConfig;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.context.RpcContext;
import org.dodo.rpc.NodesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 最优响应时间（根据响应时间计算权重）负载策略
 * @author maxlim
 *
 */
public class WeightedResponseTimeLoadBalance extends RoundRobinLoadBalance {
    private final static Logger logger = LoggerFactory.getLogger(WeightedResponseTimeLoadBalance.class);
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory(WeightedResponseTimeLoadBalance.class.getName()));

    @Override
    public String doSelect(InvokerRequest request, List<String> nodes) {
        String key = super.doSelect(request, nodes);
        RpcContext.getContext().setValue(RpcContext.CONTEXT_CONSUMER_INVOKE_USETIME + request.getClassName(), new Object[]{key, System.nanoTime()});
        return key;
    }

    public WeightedResponseTimeLoadBalance() {
        int rebuildIntervalSecond = ConfigManager.instance().getConsumerConfig().getLoadBalanceParameter(ConsumerConfig.LOAD_BALANCE_WEIGHTED_RESPONSE_TIME_INTERVAL, 30, Integer.class);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                linked.forEach((key, roundRobinLinked) -> {
                    rebuild(key, roundRobinLinked);
                    RpcContext.clearServiceInvokeNodeUseTime(key);
                });
            } catch (Exception e) {
                logger.error(null, e);
            }
        }, rebuildIntervalSecond, rebuildIntervalSecond, TimeUnit.SECONDS);
    }

    private void rebuild(String service, RoundRobinLinked roundRobinLinked) {
        List<String> nodes = NodesManager.instance().getNodes(service);
        Map<String, Integer> nodesWithWeights = new HashMap<>(nodes.size());
        //计算所有节点响应时间的总和
        int allResponseTimeTotal = 0;
        for (String node : nodes) {
            int weight = RpcContext.getServiceInvokeNodeUseTime(service, node);
            if(weight <= 0) weight = 1;
            nodesWithWeights.put(node, weight);
            allResponseTimeTotal += weight;
        }
        //所有节点响应时间总和 减去 每个节点自己的响应时间 就是每个节点自己的权重
        //节点响应时间越大，权重就越小
        for (String node : nodes) {
            nodesWithWeights.put(node, allResponseTimeTotal - nodesWithWeights.get(node));
        }
        Map<String, Integer> nodesWithWeightsOrdered = new LinkedHashMap<>(nodesWithWeights.size());
        nodesWithWeights.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEachOrdered(e -> nodesWithWeightsOrdered.put(e.getKey(), e.getValue()));
        roundRobinLinked.rebuild(nodesWithWeightsOrdered);
    }

    @Override
    public void setWeight(String weights) {
    }
}
