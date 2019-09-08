package org.dodo.provider.server;

import org.dodo.context.RpcContext;
import org.dodo.rpc.RpcServer;
import org.dodo.rpc.RpcServerHandler;

/**
 * dodo默认服务方式
 * @author maxlim
 *
 */
public class DefaultServer extends AbstractServer {

    @Override
    public void initEndpoint(String protocol, String ip, int port) {
        this.endpoint = new RpcServer(new RpcServerHandler(this.executor::execute));
        this.endpoint.listen(ip, port);
    }
}
