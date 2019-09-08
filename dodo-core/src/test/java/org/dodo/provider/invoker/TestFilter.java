package org.dodo.provider.invoker;

import org.dodo.consumer.invoker.InvokerRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author maxlim
 */
public class TestFilter {
    Invoker decorated;
    private final static int GENERAL_RETURN = 1;

    @Before
    public void init() {
        InvokerWrapper wrapper = new InvokerWrapper();
        wrapper.setInvoker(new TestInvoker());
        decorated = wrapper;
    }
    @Test
    public void testGeneral() throws Exception {
        InvokerRequest invokerRequest = new InvokerRequest();
        invokerRequest.setArgs(new Object[]{"general"});
        Object result = decorated.invoke(invokerRequest);
        Assert.assertEquals("testGeneral fail!", GENERAL_RETURN, result);
    }

    public static class TestInvoker implements Invoker {

        @Override
        public Object invoke(InvokerRequest invokerRequest) throws Exception {
            System.out.println("test provider invoker here");
            return GENERAL_RETURN;
        }
    }
}
