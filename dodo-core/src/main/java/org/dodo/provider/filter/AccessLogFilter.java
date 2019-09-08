package org.dodo.provider.filter;

import org.dodo.common.utils.StringBuilderBuffer;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.context.RpcContext;
import org.dodo.filter.Filter;
import org.dodo.filter.FilterAttribute;
import org.dodo.filter.FilterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO 动态配置过滤开关
/**
 * 开启access log会有性能损耗
 * @author maxlim
 *
 */
@FilterAttribute(group = FilterGroup.PROVIDER_FRONT, order = Integer.MIN_VALUE)
public class AccessLogFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger("access");//logger配置为异步模式

    @Override
    public String getName() {
        return "access-log";
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception {
        Object result = null;
        long usetimeMillis = 0L;
        boolean isThrowExp = false;
        try {
            long start = System.currentTimeMillis();
            result = nextInvoker.invoke(invokerRequest);
            usetimeMillis = System.currentTimeMillis() - start;
        } catch (Exception e) {
            isThrowExp = true;
            throw e;
        } finally {
            StringBuilder stringBuilder = StringBuilderBuffer.getStringBuilder();
            stringBuilder.append("group:").append(RpcContext.getContext().getValue(RpcContext.CONTEXT_GROUP));
            stringBuilder.append("|service:").append(invokerRequest.getClassName()).append(".").append(invokerRequest.getMethodName());
            stringBuilder.append("|protocol:").append(RpcContext.getContext().getValue(RpcContext.CONTEXT_PROTOCOL));
            stringBuilder.append("|remote:").append(RpcContext.getContext().getValue(RpcContext.CONTEXT_REMOTE));
            stringBuilder.append("|requestId:").append(invokerRequest.getRequestSeq());
            stringBuilder.append("|success:").append( ! isThrowExp);
            stringBuilder.append("|usetime:").append(usetimeMillis);
            logger.info(stringBuilder.toString());
        }
        return result;
    }
}
