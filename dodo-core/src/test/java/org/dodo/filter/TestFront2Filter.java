package org.dodo.filter;

import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * for test
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.CONSUMER_FRONT, order = -1)
public class TestFront2Filter implements Filter {
    @Override
    public String getName() {
        return "test2";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test front filter2...");
        if("end".equals(String.valueOf(invokerRequest.getArgs()[0]))) {
            System.out.println("be filter terminated " + this.getClass().getName());
            return "be filter terminated";
        }
        return nextInvoker.invoke(invokerRequest);
    }
}
