package org.dodo.provider.invoker;

import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.PROVIDER_POST, order = -2)
public class TestPost2Filter implements Filter {
    @Override
    public String getName() {
        return "test14";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test provider post filter2...");
        return nextInvoker.invoke(invokerRequest);
    }
}
