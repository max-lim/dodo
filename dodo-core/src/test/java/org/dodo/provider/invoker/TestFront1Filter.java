package org.dodo.provider.invoker;

import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * for test
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.PROVIDER_FRONT)
public class TestFront1Filter implements Filter {
    @Override
    public String getName() {
        return "test11";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test provider front filter1...");
        return nextInvoker.invoke(invokerRequest);
    }
}
