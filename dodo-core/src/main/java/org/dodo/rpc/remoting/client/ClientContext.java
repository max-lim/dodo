package org.dodo.rpc.remoting.client;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientContext {
	private final static Logger logger = LoggerFactory.getLogger(ClientContext.class);
	private static ClientContext context = new ClientContext();
	private ClientContext() {}
	public static ClientContext context() {
		return context;
	}
	private static Map<Long, RequestFuture> futures = new ConcurrentHashMap<>();
	private static Map<Long, ResponseCallback> callbacks = new ConcurrentHashMap<>();
	static {
		new Timer("ClientContextTimer").schedule(new TimerTask() {
			@Override
			public void run() {
				long currentTime = System.currentTimeMillis();
				futures.forEach((key, future)->{
					if (currentTime - future.getCreatedTime() >= 30_000) {
						futures.remove(key);
						callbacks.remove(key);
						logger.warn("future {} long time {} no response, and then remove it", key, future.getCreatedTime());
					}
				});
			}
		}, 30_000, 30_000);
	}
	public void addFuture(long id, RequestFuture future) {
		futures.put(id, future);
	}
	public RequestFuture removeFuture(long id) {
		return futures.remove(id);
	}
	
	public void addCallback(long id, ResponseCallback callback) {
		callbacks.put(id, callback);
	}
	public ResponseCallback removeCallback(long id) {
		return callbacks.remove(id);
	}
}
