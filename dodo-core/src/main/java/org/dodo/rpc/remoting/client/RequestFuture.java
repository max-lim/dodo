package org.dodo.rpc.remoting.client;

import java.util.concurrent.*;

import org.dodo.rpc.remoting.Message;

/**
 * 异步获取request结果
 * @author maxlim
 */
public class RequestFuture extends FutureTask<Message> {
	private long createdTime;
	public RequestFuture() {
		super(() -> null);
		createdTime = System.currentTimeMillis();
	}

	@Override
	public void set(Message message) {
		super.set(message);
	}

	@Override
	public void setException(Throwable t) {
		super.setException(t);
	}

	@Override
	public void done() {}

	public long getCreatedTime() {
		return createdTime;
	}
}
