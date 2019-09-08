package org.dodo.rpc;

import java.io.Closeable;

/**
 * 服务终端 for server
 * @author maxlim
 *
 */
public interface Endpoint extends Closeable {
    public void listen(String ip, int port);
}
