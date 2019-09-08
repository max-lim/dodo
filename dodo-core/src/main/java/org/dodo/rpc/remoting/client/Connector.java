package org.dodo.rpc.remoting.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.dodo.common.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 客户端连接器
 * @author maxlim
 *
 */
public class Connector {
	private static final Logger logger = LoggerFactory.getLogger(Connector.class);
	private String ip;
	private int port;
	private volatile Channel channel;
	private Bootstrap bootstrap;
	private int connectTimeout;
	private ChannelInitializer<SocketChannel> channelInitializer;
	private ReentrantLock connectingLock = new ReentrantLock();
	
	private static EventLoopGroup group;
	static {
		group = new NioEventLoopGroup(1,
				Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1,
						new NamedThreadFactory("client-connector")));
	}

	public Connector(String ip, int port, int connectTimeout, Class<? extends ChannelOutboundHandlerAdapter> encoder,
					 Class<? extends ChannelInboundHandlerAdapter> decoder, SimpleChannelInboundHandler<?> inboundHandler) {
		this(ip, port, connectTimeout, new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				p.addLast("decoder", decoder.newInstance());
				p.addLast("encoder", encoder.newInstance());
				p.addLast("handler", inboundHandler);
			}
		});
	}

	public Connector(String ip, int port, int connectTimeout, ChannelInitializer<SocketChannel> channelInitializer) {
		this.ip = ip;
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.channelInitializer = channelInitializer;
	}

	private void connect(String ip, int port) {
		if (connectingLock.isLocked()) {
			return ;
		}
		try {
			connectingLock.lock();

			if(this.bootstrap == null) {
				bootstrap = new Bootstrap();
				bootstrap.group(group)
						.channel(NioSocketChannel.class)
						.option(ChannelOption.TCP_NODELAY, true)
						.handler(channelInitializer);
			}
			this.ip = ip;
			this.port = port;
			ChannelFuture cf = bootstrap.connect(new InetSocketAddress(ip, port));
			cf.addListener((ChannelFutureListener) future -> {
				if(future.isSuccess()) {
					if(logger.isDebugEnabled()) {
						logger.debug("connect new channel={}", future.channel().toString());
					}
				}
			});
			this.channel = cf.channel();
		} finally {
			connectingLock.unlock();
		}
	}
	
	public boolean connect() {
		return this.connect(false);
	}

	public boolean connect(boolean force) {
		if (force) {
			this.close();
			this.connect(ip, port);
			return isConnecting();
		}
		else {
			if ( ! isConnecting()) {
				this.connect(ip, port);
				return isConnecting();
			}
			logger.info("the connection is exists and connecting");
			return true;
		}
	}

	public void close() {
		if(this.channel == null) return;
		this.channel.close();
	}

	public boolean send(Object data) {
		if(this.isConnecting()) {
			this.channel.writeAndFlush(data);
			return true;
		}
		return false;
	}

	public boolean isConnecting() {
		boolean ret = this.channel != null && this.channel.isWritable();
		if( ! ret && channel != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("connection is closed.isActive={},isOpen={},isRegistered={},isWritable={}",
						channel.isActive(), channel.isOpen(),
						channel.isRegistered(),channel.isWritable());
			}
		}
		return ret;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return "Connector{" +
				"ip='" + ip + '\'' +
				", port=" + port +
				", channel=" + channel +
				'}';
	}
}


