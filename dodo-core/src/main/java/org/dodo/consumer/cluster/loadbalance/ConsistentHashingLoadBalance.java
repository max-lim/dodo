package org.dodo.consumer.cluster.loadbalance;

import org.dodo.common.utils.ConsistentHashing;
import org.dodo.config.ConfigManager;
import org.dodo.config.ConsumerConfig;
import org.dodo.consumer.invoker.InvokerRequest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性hash负载均衡
 * @author maxlim
 *
 */
public class ConsistentHashingLoadBalance extends AbstractLoadBalance {
    private ConcurrentHashMap<String, ConsistentHashing> servicesConsistentHashing = new ConcurrentHashMap<>();
    @Override
    public String doSelect(InvokerRequest request, List<String> nodes) {
        ConsistentHashing consistentHashing = servicesConsistentHashing.get(request.getClassName());
        if(consistentHashing == null) {
            int virtualNodesNumber = ConfigManager.instance().getConsumerConfig().getLoadBalanceParameter(ConsumerConfig.LOAD_BALANCE_CONSISTENT_HASHING_VIRTUAL, 100, Integer.class);
            servicesConsistentHashing.putIfAbsent(request.getClassName(), new ConsistentHashing(nodes, virtualNodesNumber));
            consistentHashing = servicesConsistentHashing.get(request.getClassName());
        }
        else {
            consistentHashing.rebuild(nodes);
        }
        return consistentHashing.select(Arrays.toString(request.getArgs()));
    }

    @Override
    public void onRefresh(String key, String node, RefreshConst refreshConst) {
        ConsistentHashing consistentHashing = servicesConsistentHashing.get(key);
        if(consistentHashing != null) {
            if(refreshConst == RefreshConst.ADD) consistentHashing.hashingKey(node);
            else consistentHashing.removeKey(node);
        }
        else {
            if(refreshConst == RefreshConst.ADD) {
                int virtualNodesNumber = ConfigManager.instance().getConsumerConfig().getLoadBalanceParameter(ConsumerConfig.LOAD_BALANCE_CONSISTENT_HASHING_VIRTUAL, 100, Integer.class);
                ConsistentHashing old = servicesConsistentHashing.putIfAbsent(key, new ConsistentHashing(node, virtualNodesNumber));
                if(old != null) old.hashingKey(node);
            }
        }
    }
}
