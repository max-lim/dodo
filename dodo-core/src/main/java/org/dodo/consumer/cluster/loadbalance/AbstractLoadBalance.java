package org.dodo.consumer.cluster.loadbalance;

import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Node;
import org.dodo.rpc.NodeChangeListener;
import org.dodo.rpc.NodesManager;
import org.dodo.rpc.RpcException;

import java.util.List;

/**
 * @author maxlim
 *
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    public AbstractLoadBalance() {
        NodesManager.instance().registerNodeChangeListener(new NodeChangeListener() {
            @Override
            public void onNodeAdd(String service, Node node) {
                onRefresh(service, node.key(), RefreshConst.ADD);
            }

            @Override
            public void noNodeRemoved(String service, Node node) {
                onRefresh(service, node.key(), RefreshConst.REMOVE);
            }
        });
    }

    @Override
    public Node select(InvokerRequest request) throws Exception {
        List<String> nodes = NodesManager.instance().getNodes(request.getClassName());
        if(nodes.isEmpty()) new RpcException("has not vaild node be found.");
        if(nodes.size() == 1) return NodesManager.instance().getNode(nodes.get(0));
        return NodesManager.instance().getNode(doSelect(request, nodes));
    }

    public abstract String doSelect(InvokerRequest request, List<String> nodes);

    @Override
    public void setWeight(String weights) {

    }

    @Override
    public void onRefresh(String key, String node, RefreshConst refreshConst) {

    }
}
