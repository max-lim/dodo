package org.dodo.rpc;

import org.junit.Before;
import org.junit.Test;
import org.dodo.register.URL;

public class TestClient {
	RpcClient client;
	@Before
	public void init() throws Exception {
		client = new RpcClient("127.0.0.1", 9900);
		client.connect();
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
