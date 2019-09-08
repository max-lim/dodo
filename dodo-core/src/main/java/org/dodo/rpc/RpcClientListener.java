package org.dodo.rpc;


import org.dodo.rpc.remoting.ClientListener;

public class RpcClientListener implements ClientListener {
    private RpcClient rpcClient;

    public RpcClientListener(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void onConnect() {
        NodesManager.instance().addNode(rpcClient);
    }

    @Override
    public void onClose() {
        rpcClient.ping();
        NodesManager.instance().removeNode(rpcClient);
    }
}