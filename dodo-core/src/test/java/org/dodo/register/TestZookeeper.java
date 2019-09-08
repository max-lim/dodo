package org.dodo.register;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.dodo.config.ConfigManager;
import org.dodo.config.RegisterConfig;
import org.dodo.register.center.ZookeeperRegistry;
import org.dodo.register.discovery.DiscoveryListener;

public class TestZookeeper {
	ZookeeperRegistry register;
	
	@Before
	public void connect() throws Exception {
		RegisterConfig registerConfig = new RegisterConfig("zookeeper", "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183");
		ConfigManager.instance().setRegisterConfig(registerConfig);
		
		register = new ZookeeperRegistry();
		String urlstring = "dodo://localhost:8080/org.dodo.service.call?app=test&here=yes&ver=1&a-p_p=n-o_";
		URL url = URL.build(urlstring);
		register.register(url);
	}
	@After
	public void close() {
		if(register != null) {
			register.close();
		}
	}
	@Test
	public void discovery() throws Exception {
		CountDownLatch countDown = new CountDownLatch(1);
		new Thread(() -> {
			try {
				TestZookeeper.this.register.subscribe("org.dodo.service.call",new DiscoveryListener() {
					
					@Override
					public void notifyRemoved(URL url) {
						System.out.println("removed:"+url);
						countDown.countDown();
					}
					
					@Override
					public void notifyNew(URL url) {
						System.out.println("new:"+url);
						countDown.countDown();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		countDown.await();
	}
	
}
