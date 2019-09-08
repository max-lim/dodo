package org.dodo.consumer.cluster.loadbalance;

import org.dodo.common.spi.Spi;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Node;

@Spi("random")
public interface LoadBalance {
    public Node select(InvokerRequest request) throws Exception;

    /**
     * 设置节点的权重,具体内容由实现类去解析
     * @param weights json格式字符串
     */
    public void setWeight(String weights);

    public static enum RefreshConst {
        ADD,
        REMOVE,
        ;
    }

    /**
     * 节点有变化，触发刷新负载策略的节点缓存
     */
    public void onRefresh(String key, String node, RefreshConst refreshConst);
}
