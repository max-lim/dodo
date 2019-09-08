package org.dodo.provider.invoker;

import org.dodo.filter.FilterGroup;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * 服务提供者invoker包装
 * @author maxlim
 */
public class InvokerWrapper extends org.dodo.consumer.invoker.InvokerWrapper implements Invoker {
    private Invoker invoker;

    public void setInvoker(Invoker invoker) {
        //先构建后置链，拿到后置的头节点，把该节点作为前置的尾，接入前置链
        org.dodo.consumer.invoker.Invoker postHead = buildPostChain(invoker, filtersByGroupAndOrder(FilterGroup.PROVIDER_POST));
        org.dodo.consumer.invoker.Invoker frontHead = buildFrontChain(postHead, filtersByGroupAndOrder(FilterGroup.PROVIDER_FRONT));
        this.invoker = frontHead::invoke;
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest) throws Exception {
        return invoker.invoke(invokerRequest);
    }
}
