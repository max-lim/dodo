package org.dodo.register.center;


import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.Watcher;
import org.dodo.config.ConfigManager;
import org.dodo.config.RegisterConfig;
import org.dodo.register.URL;
import org.dodo.register.discovery.DiscoveryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * zookeeper实现注册中心的注册/订阅监听
 * zookeeper用3.4.x版本
 * @author maxlim
 *
 */
public class ZookeeperRegistry implements Register {
	private final static Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);
	private final static String SERVICE_ROOT_PATH = "/dodo/providers";
	private ConcurrentHashMap<String, ChildListener> listeners = new ConcurrentHashMap<>();//servicePath -> ChildListener
	private Set<URL> registeredUrls = new ConcurrentHashMap<>().newKeySet();
	private ZkClient zkClient;

	public ZookeeperRegistry() {
	}

	public void start() {
		if(zkClient == null) {
			synchronized (this) {
				if(zkClient == null) {
					RegisterConfig registerConfig = ConfigManager.instance().getRegisterConfig();
					zkClient = new ZkClient(registerConfig.getAddress(), registerConfig.getSessionTimeout(), registerConfig.getConnectTimeout(), new ZookeeperStringSerializer());
					zkClient.subscribeStateChanges(new IZkStateListener(){

						@Override
						public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
							logger.info("keeper state change:{}", state.toString());
						}

						@Override
						public void handleNewSession() throws Exception {
							refreshRegister();
							refreshSubscribe();
						}

						@Override
						public void handleSessionEstablishmentError(Throwable error) throws Exception {
							logger.warn(null, error);
						}
					});
					logger.info("zookeeper register start,config={}", registerConfig.toString());
				}
			}
		}
	}

	/**
	 * 服务父节点+ip:port=服务节点 /dodo/providers/xxxservice/8.8.8.8:8000
	 * @param url
	 * @return
	 */
	private String nodePath(URL url) {
		return new StringBuilder().append(SERVICE_ROOT_PATH).append("/").append(url.getUniq()).toString();
	}

	/**
	 * 服务父节点 /dodo/providers/xxxservice
	 * @param url
	 * @return
	 */
	private String servicePath(URL url) {
		return new StringBuilder().append(SERVICE_ROOT_PATH).append("/").append(url.getPath()).toString();
	}

	private String servicePath(String service) {
		return SERVICE_ROOT_PATH + "/" + service;
	}

	@Override
	public void register(URL url) throws Exception {
		start();

		String typePath = servicePath(url);
		if( ! zkClient.exists(typePath)) {
			zkClient.createPersistent(typePath,true);
		}
		String nodePath = nodePath(url);
		try {
			zkClient.createEphemeral(nodePath, url.toString());
		}catch (Exception e) {
			if(e instanceof ZkNodeExistsException) {
				zkClient.writeData(nodePath, url.toString());
			}
			else throw e;
		}
		registeredUrls.add(url);
	}

	@Override
	public void unregister(URL url) throws Exception {
		start();

		String path = nodePath(url);
		if(zkClient.exists(path)) {
			zkClient.delete(path);
		}
	}

	private void doSubscribe(String servicePath, DiscoveryListener listener) {
		ChildListener childListener = listeners.get(listener);
		if(childListener == null) {
			synchronized (this) {
				childListener = listeners.get(listener);
				if(childListener == null) {
					listeners.putIfAbsent(servicePath, new ChildListener(listener));
				}
			}
		}
		zkClient.subscribeChildChanges(servicePath, childListener);
		zkClient.subscribeDataChanges(servicePath, childListener);
	}

	/**
	 * 消费监听到的数据，通知给发现者监听器
	 */
	private BiConsumer<String, DiscoveryListener> notify = new BiConsumer<String, DiscoveryListener>() {
		@Override
		public void accept(String path, DiscoveryListener listener) {
			String data = zkClient.readData(path,true);
			if(logger.isDebugEnabled()) {
				logger.debug("path={},data={}",path, data);
			}
			if(data != null) {
				listener.notifyNew(URL.build(data));
			}
		}
	};

	/**
	 * 同步获取数据
	 * @param servicePath
	 * @param listener
	 */
	private void getCurrentChids(String servicePath, DiscoveryListener listener) {
		List<String> currentChilds = Collections.emptyList();
		String parentPath = servicePath;
		if (zkClient.exists(parentPath)) {
			currentChilds = zkClient.getChildren(parentPath);
		}
		currentChilds.forEach(child -> {
			notify.accept(parentPath+"/"+new String(child.getBytes()), listener);
		});
	}

	/**
	 * service是服务父节点 /dodo/providers/xxxservice 中的service
	 * @param service 服务
	 * @param discoveryListener
	 * @throws Exception
	 */
	@Override
	public void subscribe(String service, DiscoveryListener discoveryListener) throws Exception {
		start();
		String servicePath = servicePath(service);
		doSubscribe(servicePath, discoveryListener);
		getCurrentChids(servicePath, discoveryListener);
	}

	public void unsubscribe(String servicePath, IZkChildListener zkChildListener) {
		if(zkChildListener != null) {
			zkClient.unsubscribeChildChanges(servicePath, zkChildListener);
		}
	}

	@Override
	public void close() {
		for(Map.Entry<String, ChildListener> entry: listeners.entrySet()) {
			unsubscribe(entry.getKey(), entry.getValue());
		}
		if (zkClient != null) {
			zkClient.close();
		}
	}

	/**
	 * 重新注册
	 */
	public void refreshRegister() {
		for(URL url: registeredUrls) {
			try {
				register(url);
			} catch (Exception e) {
				logger.warn(url.toString(), e);
			}
		}
	}

	/**
	 * 重新订阅
	 */
	public void refreshSubscribe() {
		for(Map.Entry<String, ChildListener> entry: listeners.entrySet()) {
			try {
				doSubscribe(entry.getKey(), entry.getValue().discoveryListener);
			} catch (Exception e) {
				logger.warn(null, e);
			}
		}
	}

	private class ChildListener implements IZkChildListener, IZkDataListener {
		DiscoveryListener discoveryListener;

		public ChildListener(DiscoveryListener discoveryListener) {
			this.discoveryListener = discoveryListener;
		}

		@Override
		public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
			currentChilds.forEach(child -> {
				ZookeeperRegistry.this.notify.accept(parentPath+"/"+new String(child.getBytes()), discoveryListener);
			});
		}

		@Override
		public void handleDataChange(String dataPath, Object data) throws Exception {
			if(logger.isDebugEnabled()) {
				logger.debug("data change path={}, data={}", dataPath, data);
			}
			discoveryListener.notifyNew(URL.build((String) data));
		}

		@Override
		public void handleDataDeleted(String dataPath) throws Exception {
			if(logger.isDebugEnabled()) {
				logger.debug("removed path {}", dataPath);
			}
			discoveryListener.notifyRemoved(URL.build(dataPath));
		}
	}
}
