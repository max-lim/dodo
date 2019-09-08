package org.dodo.register.discovery;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;
import org.dodo.register.URL;
import org.dodo.register.center.Register;
import org.dodo.rpc.NodesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * 注册监听注册中心，发现服务的变化
 * @author maxlim
 *
 */
public class Discovery implements Closeable {
	private final static Logger logger = LoggerFactory.getLogger(Discovery.class);
	private static final Discovery INSTANCE = new Discovery();
	public static Discovery instance() {
		return INSTANCE;
	}
	
	private final DiscoveryListener listener = new DiscoveryListener() {

		@Override
		public void notifyNew(URL url) {
			NodesManager.instance().addNode(url);
		}

		@Override
		public void notifyRemoved(URL url) {
			NodesManager.instance().onRemovedByDiscovery(url);
		}
		
	};
	
	/**
	 * 触发注册中心订阅监听
	 * @throws DiscoveryException
	 */
	public void discover(String service) throws DiscoveryException {
		try {
			Register register = SpiLoader.getExtensionHolder(Register.class).get(ConfigManager.instance().getRegisterConfig().getName());
			register.subscribe(service, listener);
		} catch (Exception e) {
			throw new DiscoveryException(service, e);
		}
	}
	
	/**
	 * 取消注册中心订阅监听，关闭注册中心客户端连接
	 */
	public void undiscover() throws IOException {
		Register register = SpiLoader.getExtensionHolder(Register.class).get(ConfigManager.instance().getRegisterConfig().getName());
		register.close();
	}

	@Override
	public void close() throws IOException {
		undiscover();
	}

}
