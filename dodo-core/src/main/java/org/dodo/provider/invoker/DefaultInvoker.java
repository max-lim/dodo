package org.dodo.provider.invoker;

import org.dodo.common.spi.SpiLoader;
import org.dodo.common.spi.Wrapper;
import org.dodo.config.ConfigManager;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Response;
import org.dodo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务提供者默认调用器
 * @author maxlim
 *
 */
@Wrapper("wrapper")
public class DefaultInvoker implements Invoker {
    private static final Logger logger = LoggerFactory.getLogger(DefaultInvoker.class);
    private ServiceInvoker serviceInvoker;
    public DefaultInvoker() {
        serviceInvoker = SpiLoader.getExtensionHolder(ServiceInvoker.class).get(ConfigManager.instance().getProviderConfig().getReflect());
    }

    @Override
    public Object invoke(InvokerRequest invokerRequest) throws Exception {
        Response response = new Response();
        try {
            Object result = serviceInvoker.invoke(invokerRequest.getClassName(), invokerRequest.getMethodName(), invokerRequest.getArgs());
            response.setResult(result);
            if(result != null) response.setResultType(result.getClass().getName());
        } catch (Exception e) {
            logger.error("execute class:"+invokerRequest.getClassName()+"."+invokerRequest.getMethodName(), e);
            response.setException(new RpcException("execute class:"+invokerRequest.getClassName()+"."+invokerRequest.getMethodName(), e));
        }
        return response;
    }
}
