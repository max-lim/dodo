package org.dodo.consumer.cluster.strategy;

import org.dodo.common.spi.Wrapper;
import org.dodo.consumer.cluster.AbstractClusterInvoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 失效安全集群调用方式
 * @author maxlim
 *
 */
@Wrapper("wrapper")
public class FailsafeClusterInvoker extends AbstractClusterInvoker {
    private final static Logger logger = LoggerFactory.getLogger(FailsafeClusterInvoker.class);

    @Override
    public Object invoke(InvokerRequest invokerRequest, Node serverNode) throws Exception {
        boolean async = this.isAsync(invokerRequest);
        long timeout = this.timeout(invokerRequest, serverNode);
        try {
            return serverNode.send(invokerRequest, timeout, async);
        } catch (Exception e) {
            logger.warn("invoke fail: " + invokerRequest.toString(), e);
        }
        return null;
    }
}
