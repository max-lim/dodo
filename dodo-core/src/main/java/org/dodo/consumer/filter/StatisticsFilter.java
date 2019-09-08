package org.dodo.consumer.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.context.RpcContext;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;

/**
 * 统计
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.CONSUMER_FRONT, order = Integer.MAX_VALUE)
public class StatisticsFilter implements Filter {
    @Override
    public String getName() {
        return "statistics";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        //统计节点调用耗时
        Object[] startInfo = (Object[]) RpcContext.getContext().getValue(RpcContext.CONTEXT_CONSUMER_INVOKE_USETIME + invokerRequest.getClassName());
        if (startInfo != null) {
            String nodeKey = (String) startInfo[0];
            int invokeUseTime = (int) ((System.nanoTime() - (long)startInfo[1])/1000/1000);
            RpcContext.putServiceInvokeNodeUseTime(invokerRequest.getClassName(), nodeKey, invokeUseTime);
        }
        return nextInvoker.invoke(invokerRequest);
    }
}
