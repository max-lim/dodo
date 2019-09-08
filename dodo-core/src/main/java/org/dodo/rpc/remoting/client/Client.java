package org.dodo.rpc.remoting.client;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import org.dodo.rpc.remoting.*;

/**
 * 客户端
 * @author maxlim
 *
 */
public class Client extends Connector {
	ClientHeartbeat heartbeat;

	public Client(String ip, int port, int connectTimeout, Class<? extends ChannelOutboundHandlerAdapter> encoder,
				  Class<? extends ChannelInboundHandlerAdapter> decoder, ClientListener statusHandler) {
		super(ip, port, connectTimeout, encoder, decoder, new ClientInboundHandler(statusHandler, HeartbeatData.MESSAGE_HEARTBEAT));
		this.heartbeat = new ClientHeartbeat(this, 5*1000, HeartbeatData.MESSAGE_HEARTBEAT);
	}

	public Client(String ip, int port, int connectTimeout, ClientListener statusHandler) {
		this(ip, port, connectTimeout, Encoder.class, Decoder.class, statusHandler);
	}

	public boolean connect() {
		return connect(false);
	}

	public boolean connect(boolean force) {
		boolean flag = super.connect(force);
		this.heartbeat.start();
		return flag;
	}

	public void ping() {
		heartbeat.ping();
	}

	/**
	 * 拿到future，可以进行阻塞等待响应
	 * @param message
	 * @return
	 */
	public RequestFuture send(Message message) {
		if(super.send(message)) {
			RequestFuture future = new RequestFuture();
			ClientContext.context().addFuture(message.getSeq(), future);
			return future;
		}
		return null;
	}

	/**
	 * 注册回调函数，进行异步响应处理
	 * @param message
	 * @param callback
	 * @return
	 */
	public boolean send(Message message, ResponseCallback callback) {
		RequestFuture future = this.send(message);
		if(future != null) {
			ClientContext.context().addCallback(message.getSeq(), callback);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Client [ip=" + getIp() + ", port=" + getPort() + "]";
	}

}
