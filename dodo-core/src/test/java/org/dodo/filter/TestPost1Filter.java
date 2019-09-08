package org.dodo.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * @author maxlim
 */
@FilterAttribute(group = FilterGroup.CONSUMER_POST, order = -1)
public class TestPost1Filter implements Filter {
    @Override
    public String getName() {
        return "test3";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test post filter1...");
        if(nextInvoker == null) return "post1";
        return nextInvoker.invoke(invokerRequest);
    }
}
