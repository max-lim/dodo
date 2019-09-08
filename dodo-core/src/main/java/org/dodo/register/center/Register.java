package org.dodo.register.center;


import org.dodo.common.spi.Spi;
import org.dodo.register.URL;
import org.dodo.register.discovery.DiscoveryListener;

import java.io.Closeable;

/**
 * 注册中心接口
 * @author maxlim
 *
 */
@Spi("zookeeper")
public interface Register extends Closeable {
    /**
     * 注册url
     * @param url
     * @throws Exception
     */
    public void register(URL url) throws Exception;

    /**
     * 注销url
     * @param url
     * @throws Exception
     */
    public void unregister(URL url) throws Exception;

    /**
     * 订阅指定的service
     * @param service
     * @param listener
     * @throws Exception
     */
    public void subscribe(String service, DiscoveryListener listener) throws Exception;

}
