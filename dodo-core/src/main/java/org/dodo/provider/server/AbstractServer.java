package org.dodo.provider.server;

import org.dodo.rpc.Endpoint;
import org.dodo.rpc.serialize.Serialization;

import java.io.IOException;

/**
 * 服务
 * @author maxlim
 *
 */
public abstract class AbstractServer implements Server {
    protected Executor executor;
    protected Endpoint endpoint;
    protected String name;

    @Override
    public void export(String name, String protocol, String ip, int port, int corePoolSize, int maxPoolSize, int workQueueSize, int accepts, Serialization serialization) {
        this.name = name;
        this.initExecutor(corePoolSize, maxPoolSize, workQueueSize, accepts, serialization);
        this.initEndpoint(protocol, ip, port);
    }

    public void initExecutor(int corePoolSize, int maxPoolSize, int workQueueSize, int accepts, Serialization serialization) {
        this.executor = new Executor.Builder().setName(name).setCorePoolSize(corePoolSize)
                .setMaxPoolSize(maxPoolSize)
                .setWorkQueueSize(workQueueSize)
                .setWorkQueueSize(workQueueSize)
                .setSerialization(serialization).build();
    }

    /**
     * 初始化服务
     * @param protocol
     * @param ip
     * @param port
     */
    public abstract void initEndpoint(String protocol, String ip, int port);

    @Override
    public void close() throws IOException {
        if(this.executor != null) this.executor.close();
        if(this.endpoint != null) this.endpoint.close();
    }
}
