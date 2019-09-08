package org.dodo.rpc.remoting.client;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import org.dodo.common.thread.NamedThreadFactory;
import org.dodo.rpc.remoting.HeartbeatData;
import org.dodo.rpc.remoting.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

/**
 * 客户端心跳处理
 * @author maxlim
 *
 */
public class ClientHeartbeat implements TimerTask, Closeable {
	private static final Logger logger = LoggerFactory.getLogger(ClientHeartbeat.class);

	private Client client;
	private int interval;
	private long sentAt;
	private volatile long receivedPongAt;
	private Timeout pingTimeout;
	private volatile boolean isClosed = false;
	private static HashedWheelTimer timer = new HashedWheelTimer(new NamedThreadFactory("client-heartbeat"));
	private HeartbeatData<Message> heartbeatData;

	public ClientHeartbeat(Client client, int interval, HeartbeatData heartbeatData) {
		this.client = client;
		this.interval = interval;
		this.heartbeatData = heartbeatData;
	}
	public synchronized void start() {
		if( !isClosed && (this.pingTimeout == null || this.pingTimeout.isCancelled() || this.pingTimeout.isExpired())) {
			this.pingTimeout = timer.newTimeout(this, this.interval, TimeUnit.MILLISECONDS);
		}
	}
	@Override
	public void run(Timeout timeout) {
		if(isClosed) {
			return;
		}
		try {
			this.checkAndPing();
		} catch (Exception e) {
			logger.error(null, e);
		} finally {
			start();
		}
	}
	private void checkAndPing() {
		if (client.isConnecting()) {
			//pong timeout
			if ((receivedPongAt == 0 && sentAt > 0) || receivedPongAt - sentAt < 0) {
				if(logger.isDebugEnabled()) {
					logger.debug("pong timeout and retry to connect {}", client.toString());
				}
				client.connect(true);
				return;
			}
			ping();
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug("connection is closed and retry to connect {}", client.toString());
			}
			client.connect(true);
		}
	}
	public void ping() {
		client.send(heartbeatData.ping(), message -> {
			ClientHeartbeat.this.receivedPongAt = System.currentTimeMillis();
			if(logger.isDebugEnabled()) {
				logger.debug("{} received pong seq:{}. at:{}", client.toString(), message.getSeq(), ClientHeartbeat.this.receivedPongAt);
			}
		});
	}
	@Override
	public void close() {
		isClosed = true;
		if(pingTimeout != null) {
			pingTimeout.cancel();
		}
	}
}
