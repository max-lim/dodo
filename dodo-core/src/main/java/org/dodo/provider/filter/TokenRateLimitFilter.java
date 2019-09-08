package org.dodo.provider.filter;

import org.dodo.common.utils.TokenRateLimiter;
import org.dodo.config.ConfigManager;
import org.dodo.config.FiltersConfig;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.dodo.provider.ProviderException;

/**
 * 令牌桶限速过滤器，开启了RateLimitFilter就不要开启TokenRateLimitFilter
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.PROVIDER_FRONT, order = Integer.MIN_VALUE+1)
public class TokenRateLimitFilter extends RateLimitFilter {
    private TokenRateLimiter tokenRateLimiter;
    public TokenRateLimitFilter() {
        int capacity = ConfigManager.instance().getFiltersConfig().getParameter(FiltersConfig.PARAMETER_TOKEN_RATE_LIMIT_CAPACITY, 10000, Integer.class);
        int ratePerSecond = ConfigManager.instance().getFiltersConfig().getParameter(FiltersConfig.PARAMETER_TOKEN_RATE_LIMIT_RATE_PER_SECOND, 5000, Integer.class);
        tokenRateLimiter = new TokenRateLimiter(capacity, ratePerSecond);
    }

    @Override
    public String getName() {
        return "token-rate-limit";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        if( ! tokenRateLimiter.acquire()) {
            throw new ProviderException("request be limited by token-rate-limit-filter", ProviderException.ERROR_REQUEST_LIMITED);
        }
        return super.invokeWithStat(invokerRequest, nextInvoker);
    }
}
