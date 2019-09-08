package org.dodo.consumer.filter;

import org.dodo.config.MethodConfig;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;

import java.lang.reflect.InvocationTargetException;

/**
 * mock可以用来降级/调试等
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.CONSUMER_FRONT, order = Integer.MIN_VALUE)
public class MockFilter implements Filter {
    @Override
    public String getName() {
        return "mock";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        MethodConfig methodConfig = invokerRequest.getReferenceConfig().getMethodConfig(invokerRequest.getMethodName());
        if (methodConfig != null && methodConfig.isMock()) {
            if (methodConfig.isForceMock()) {
                return doMock(invokerRequest, methodConfig);
            }
            try {
                return nextInvoker.invoke(invokerRequest);
            } catch (Exception e) {
                return doMock(invokerRequest, methodConfig);
            }
        }
        return nextInvoker.invoke(invokerRequest);
    }

    private Object doMock(InvokerRequest invokerRequest, MethodConfig methodConfig) throws InvocationTargetException, IllegalAccessException {
        return methodConfig.getOnMockMethod().invoke(methodConfig.getRef(), invokerRequest.getArgs());
    }
}
