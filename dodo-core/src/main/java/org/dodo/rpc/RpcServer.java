package org.dodo.rpc;

import org.dodo.rpc.remoting.HeartbeatData;
import org.dodo.rpc.remoting.ServerHandler;
import org.dodo.rpc.remoting.server.Server;

public class RpcServer extends Server implements Endpoint {
	
	public RpcServer(ServerHandler serverHandler) {
		int ioWorkerCount = Runtime.getRuntime().availableProcessors() + 1;
		int idleTimeoutSeconds = 30 * 1000;
		super.init(ioWorkerCount, serverHandler, HeartbeatData.MESSAGE_HEARTBEAT, idleTimeoutSeconds);
	}

}
