package org.dodo.provider.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;

/**
 * 调用链trace
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.PROVIDER_POST, order = Integer.MAX_VALUE)
public class ProviderTraceFilter implements Filter {
    @Override
    public String getName() {
        return "provider-trace";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        //nextInvoker is null of last filter
        //TODO consumer trace filter
        if (nextInvoker != null) {
            return nextInvoker.invoke(invokerRequest);
        }
        return null;
    }
}
