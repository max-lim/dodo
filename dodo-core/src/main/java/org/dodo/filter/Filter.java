package org.dodo.filter;

import org.dodo.common.spi.Spi;
import org.dodo.consumer.invoker.Invoker;
import org.dodo.consumer.invoker.InvokerRequest;

/**
 * 过滤器
 * @author maxlim
 *
 */
@Spi("")
public interface Filter {
    /**
     * 这里的名字和 spi配置文件以及FilterConfig 的保持一致
     * @return
     */
    public String getName();
    /**
     * 执行过滤
     * @param invokerRequest 执行请求内容
     * @param nextInvoker 下一个执行器
     * @return
     * @throws Exception
     */
    public Object invoke(InvokerRequest invokerRequest, Invoker nextInvoker) throws Exception;
}
