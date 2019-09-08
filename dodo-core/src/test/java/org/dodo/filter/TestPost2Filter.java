package org.dodo.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.CONSUMER_POST, order = -2)
public class TestPost2Filter implements Filter {
    @Override
    public String getName() {
        return "test4";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test post filter2...");
        return nextInvoker.invoke(invokerRequest);
    }
}
