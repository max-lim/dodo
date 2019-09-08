package org.dodo.consumer.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;

/**
 * 调用链trace
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.CONSUMER_FRONT, order = Integer.MIN_VALUE + 1)
public class ConsumerTraceFilter implements Filter {
    @Override
    public String getName() {
        return "consumer-trace";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        //TODO consumer trace filter
        if (nextInvoker != null) {
            return nextInvoker.invoke(invokerRequest);
        }
        return null;
    }
}
