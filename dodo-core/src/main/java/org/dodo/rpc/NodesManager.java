package org.dodo.rpc;

import org.dodo.register.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 服务节点管理，供消费者端使用
 * @author maxlim
 *
 */
public class NodesManager implements Closeable {
	private final static Logger logger = LoggerFactory.getLogger(NodesManager.class);
	private NodesManager() {}
	private static class Single {
		private static final NodesManager INSTANCE = new NodesManager();
	}
	public static NodesManager instance() {
		return Single.INSTANCE;
	}

	//service -> node-key
	private final ConcurrentHashMap<String, Set<String>> service2nodes = new ConcurrentHashMap<>();
	//node-key -> rpc-client
	private final ConcurrentHashMap<String, Node> nodes = new ConcurrentHashMap<>();

	private final List<NodeChangeListener> listeners = new CopyOnWriteArrayList<>();

	/**
	 * 发现新节点
	 * @param url
	 */
	public void addNode(URL url) {
		try {
			if(nodes.get(url.address()) == null) {
				Node node = new RpcClient(url.getHost(), url.getPort());
				Node nodeOld = nodes.putIfAbsent(node.key(), node);
				if(nodeOld == null) {
					node.connect();
				}
			}
		}
		finally {
			Set<String> keysOfNodes = service2nodes.get(url.getPath());
			if(keysOfNodes == null) {
				service2nodes.putIfAbsent(url.getPath(), new ConcurrentHashMap<>().newKeySet());
				keysOfNodes = service2nodes.get(url.getPath());
			}
			keysOfNodes.add(url.address());
		}
	}
	
	/**
	 * 节点被服务发现挪掉了，这里触发一下心跳
	 * @param url
	 */
	public void onRemovedByDiscovery(URL url) {
        Node node = this.nodes.get(url.address());
		if(node != null) {
            node.ping();
		}
	}
	
	public List<String> getNodes(String service) {
		List<String> vaildNodes = Collections.emptyList();
		if(service2nodes.get(service) != null) {
			vaildNodes = service2nodes.get(service).stream().filter(nodeKey -> nodes.get(nodeKey).isConnecting()).collect(Collectors.toList());
		}
		return Collections.unmodifiableList(vaildNodes);
	}

	public Node getNode(String key) {
		return nodes.get(key);
	}

	public void registerNodeChangeListener(NodeChangeListener nodeChangeListener) {
		this.listeners.add(nodeChangeListener);
	}

	public void addNode(Node node) {
		//节点变化，通知负载策略
		service2nodes.forEach((service, nodeKeys) -> {
			if(nodeKeys.contains(node.key())) {
				listeners.forEach(listener -> listener.onNodeAdd(service, node));
			}
		});
	}

	/**
	 * 触发节点删除（网络断开）
	 * @param node
	 */
	public void removeNode(Node node) {
		if(logger.isDebugEnabled()) {
			logger.debug("provider server node removed:{}", node.key());
		}
		//节点变化，通知负载策略
		service2nodes.forEach((service, nodeKeys) -> {
			if(nodeKeys.contains(node.key())) {
				listeners.forEach(listener -> listener.noNodeRemoved(service, node));
			}
		});
	}

	public void close() {
		nodes.forEach((k, node) -> {
			try {
				node.close();
			}
			catch (Exception e){
			}
		});
	}
}
