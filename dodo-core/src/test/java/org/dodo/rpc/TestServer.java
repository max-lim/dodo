package org.dodo.rpc;

import org.dodo.provider.server.Executor;
import org.junit.Before;
import org.junit.Test;

public class TestServer {
	RpcServer server = null;
	@Before
	public void init() throws Exception {
		server = new RpcServer(Executor.INSTANCE::execute);
		server.listen("0.0.0.0", 9900);
	}
	@Test
	public void test() {
		while(true) {
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
			}
		}
	}
}
