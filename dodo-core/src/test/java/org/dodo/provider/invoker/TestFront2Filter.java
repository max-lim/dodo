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
@FilterAttribute(group = FilterGroup.PROVIDER_FRONT, order = -1)
public class TestFront2Filter implements Filter {
    @Override
    public String getName() {
        return "test12";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        System.out.println("for test provider front filter2...");
        if("end".equals(String.valueOf(invokerRequest.getArgs()[0]))) {
            System.out.println("be filter terminated " + this.getClass().getName());
            return "be filter terminated";
        }
        return nextInvoker.invoke(invokerRequest);
    }
}
