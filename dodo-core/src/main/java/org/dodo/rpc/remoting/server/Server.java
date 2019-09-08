package org.dodo.rpc.remoting.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.dodo.rpc.RpcServerHandler;
import org.dodo.rpc.remoting.Decoder;
import org.dodo.rpc.remoting.Encoder;
import org.dodo.rpc.remoting.HeartbeatData;
import org.dodo.rpc.remoting.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * 服务端
 * @author maxlim
 *
 */
public class Server implements Closeable {
	private static Logger logger = LoggerFactory.getLogger(Server.class);
	private static final int DEFAULT_RECEIVE_BUFFER_SIZE = 1024 * 128;
	private static final int DEFAULT_SEND_BUFFER_SIZE = 1024 * 128;
	private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
	private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
	private ServerBootstrap bootstrap;
	private Channel server;

	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	
	public void init(int ioWorkerCount, ChannelInitializer<Channel> channelInitializer) {
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup(ioWorkerCount);
		Class<? extends ServerChannel> serverSocketChannel = NioServerSocketChannel.class;
        
        this.bootstrap = new ServerBootstrap();
		this.bootstrap.group(this.bossGroup, this.workerGroup)
				.channel(serverSocketChannel)
				.childHandler(channelInitializer)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childOption(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_RCVBUF, this.receiveBufferSize)
				.childOption(ChannelOption.SO_SNDBUF, this.sendBufferSize)
				;
	}
	/**
	 *
	 * @param ioWorkerCount io线程数
	 * @param encoder 编码器
	 * @param decoder 解码器
	 * @param serverHandler 服务网络事件
	 * @param idleTimeoutSeconds   空闲连接超时 建议值180s~240s
	 */
	public void init(int ioWorkerCount,
					 final Class<? extends ChannelOutboundHandlerAdapter> encoder,
					 final Class<? extends ChannelInboundHandlerAdapter> decoder,
					 final ServerHandler serverHandler,
					 final HeartbeatData heartbeatData,
					 final int idleTimeoutSeconds) {

		ServerInboundHandler serverInboundHandler = new ServerInboundHandler(serverHandler);
		this.init(ioWorkerCount, new ChannelInitializer<Channel>(){
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				if(idleTimeoutSeconds > 0) {
					pipeline.addLast("idleStateHandler", new IdleStateHandler(idleTimeoutSeconds, idleTimeoutSeconds, idleTimeoutSeconds));
				}
				pipeline.addLast("encoder", encoder.newInstance());
				pipeline.addLast("decoder", decoder.newInstance());
				pipeline.addLast("heartbeat", new HeartbeatHandler(heartbeatData));
				pipeline.addLast("handler", serverInboundHandler);
			}
		});
	}
	/**
	 *
	 * @param ioWorkerCount io线程数
	 * @param serverHandler 服务网络事件
	 * @param idleTimeoutSeconds   空闲连接超时 建议值180s~240s
	 */
	public void init(int ioWorkerCount,
					 final ServerHandler serverHandler,
					 final HeartbeatData heartbeatData,
					 final int idleTimeoutSeconds) {

		init(ioWorkerCount, Encoder.class, Decoder.class, serverHandler, heartbeatData, idleTimeoutSeconds);
	}

	public void listen(String ip, int port) {
		ChannelFuture f = bootstrap.bind(ip, port);
		server = f.syncUninterruptibly().channel();
		if(logger.isDebugEnabled()) {
			logger.debug("listen at {}:{}", ip, port);
		}
	}

	@Override
	public void close() {
		try {
			if (this.server != null) {
				try {
					this.server.close().await().syncUninterruptibly();
				} catch (InterruptedException e) {
				}
			}
		} finally {
			if(workerGroup!=null)workerGroup.shutdownGracefully();
			if(bossGroup!=null)bossGroup.shutdownGracefully();
		}
	}

}
