package org.dodo.provider.server;

import org.dodo.common.spi.Spi;
import org.dodo.rpc.serialize.Serialization;

import java.io.Closeable;

/**
 * 服务
 * @author maxlim
 *
 */
@Spi("dodo")
public interface Server extends Closeable {
    public void export(String name, String protocol, String ip, int port, int corePoolSize, int maxPoolSize, int workQueueSize, int accepts, Serialization serialization);
}
