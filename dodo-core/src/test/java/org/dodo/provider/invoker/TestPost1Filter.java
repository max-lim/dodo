package org.dodo.provider.invoker;

import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.PROVIDER_POST, order = -1)
public class TestPost1Filter implements Filter {
    @Override
    public String getName() {
        return "test13";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test provider post filter1...");
        if(nextInvoker == null) return "post1";
        return nextInvoker.invoke(invokerRequest);
    }
}
