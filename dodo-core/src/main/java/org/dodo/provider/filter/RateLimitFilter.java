package org.dodo.provider.filter;

import org.dodo.config.ConfigManager;
import org.dodo.config.FiltersConfig;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.context.RpcContext;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.dodo.provider.ProviderException;

/**
 * 限速过滤器：开启了TokenRateLimitFilter就不要开启RateLimitFilter
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.PROVIDER_FRONT, order = Integer.MIN_VALUE+2)
public class RateLimitFilter implements Filter {
    @Override
    public String getName() {
        return "rate-limit";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        int configLimited = ConfigManager.instance().getFiltersConfig().getParameter(FiltersConfig.PARAMETER_RATE_LIMIT_LIMITED, 100000, Integer.class);
        if(configLimited > 0 && RpcContext.getRunningCount(invokerRequest.getClassName()) >= configLimited) {
            throw new ProviderException("request be limited by rate-limit-filter.config upper limit is " + configLimited, ProviderException.ERROR_REQUEST_LIMITED);
        }
        return invokeWithStat(invokerRequest, nextInvoker);
    }

    protected Object invokeWithStat(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        Object result = null;
        long start = System.currentTimeMillis();
        try {
            RpcContext.onServiceInvokedStatBefore(invokerRequest.getClassName());
            result = nextInvoker.invoke(invokerRequest);
            RpcContext.onServiceInvokedStatAfter(invokerRequest.getClassName(), true, System.currentTimeMillis() - start);
        } catch (Exception e) {
            RpcContext.onServiceInvokedStatAfter(invokerRequest.getClassName(), false, System.currentTimeMillis() - start);
            throw e;
        }
        return result;
    }
}
