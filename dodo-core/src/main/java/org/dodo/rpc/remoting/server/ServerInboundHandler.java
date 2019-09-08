package org.dodo.rpc.remoting.server;

import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import org.dodo.rpc.remoting.Message;
import org.dodo.rpc.remoting.Message.Command;
import org.dodo.rpc.remoting.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 服务端handler
 * @author maxlim
 *
 */
@Sharable
public class ServerInboundHandler extends SimpleChannelInboundHandler<Message> {
	private static final Logger logger = LoggerFactory.getLogger(ServerInboundHandler.class);
	private ServerHandler serverHandler;

	public ServerInboundHandler(ServerHandler serverHandler) {
		this.serverHandler = serverHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		if (logger.isDebugEnabled()) {
			logger.debug("connect {}", ctx.channel().toString());
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		if (logger.isDebugEnabled()) {
			logger.debug("disconnect {}", ctx.channel().toString());
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		if(msg.getType() == Command.REQUEST) {
			msg.setRemoteIp(((InetSocketAddress)ctx.channel().remoteAddress()).getHostString());
			serverHandler.onReceive(msg, responseMessage -> ctx.channel().writeAndFlush(responseMessage));
		}
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	logger.error(ctx.channel().toString(), cause);
    }
}
