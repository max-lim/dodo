package org.dodo.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * for test
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.CONSUMER_FRONT)
public class TestFront1Filter implements Filter {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test front filter1...");
        return nextInvoker.invoke(invokerRequest);
    }
}
