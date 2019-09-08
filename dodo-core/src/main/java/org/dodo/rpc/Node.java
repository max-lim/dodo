package org.dodo.rpc;

import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.register.URL;

import java.io.Closeable;

/**
 * 服务节点 for client
 * @author maxlim
 *
 */
public interface Node extends Closeable {
    public void putURL(URL url);

    public URL getURL(String service);

    public void connect();

    public boolean isConnecting();

    public void ping();

    public String key();

    public String address();

    /**
     * rpc发送入口
     * @param invokerRequest
     * @param timeout 超时时间
     * @param async 是否异步，是异步，需要配置接收异步结果的函数
     * @param <T>
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T send(InvokerRequest invokerRequest, long timeout, boolean async) throws Exception;
}
