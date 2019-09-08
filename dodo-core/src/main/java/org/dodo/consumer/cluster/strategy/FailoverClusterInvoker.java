package org.dodo.consumer.cluster.strategy;


import org.dodo.common.spi.Wrapper;
import org.dodo.consumer.cluster.AbstractClusterInvoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 故障转移集群调用方式
 * @author maxlim
 *
 */
@Wrapper("wrapper")
public class FailoverClusterInvoker extends AbstractClusterInvoker {
    private final static Logger logger = LoggerFactory.getLogger(FailoverClusterInvoker.class);

    @Override
    public Object invoke(InvokerRequest invokerRequest, Node serverNode) throws Exception {
        boolean async = this.isAsync(invokerRequest);
        long timeout = this.timeout(invokerRequest, serverNode);
        Exception exception = null;
        for (int i = 0; i < this.retry(invokerRequest, serverNode); i++) {
            try {
                return serverNode.send(invokerRequest, timeout, async);
            } catch (Exception e) {
                logger.warn("invoke "+ invokerRequest.toString() +" retry counter:"+i, e);
                exception = e;
                continue;
            }
        }
        throw exception;
    }
}
