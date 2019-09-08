package org.dodo.rpc.remoting.server;

import java.io.IOException;

import org.dodo.rpc.remoting.HeartbeatData;
import org.dodo.rpc.remoting.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;

/**
 * 心跳包及空闲状态处理
 * @author maxlim
 *
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);
	private HeartbeatData heartbeatData;

	public HeartbeatHandler(HeartbeatData heartbeatData) {
		this.heartbeatData = heartbeatData;
	}

	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
        	if(heartbeatData.isPing(msg)) {
				if(logger.isDebugEnabled()) {
					logger.debug("received ping");
				}
        		ctx.writeAndFlush(heartbeatData.pong(msg));
        		return;
        	}
        	else if(heartbeatData.isPong(msg)) {
        		if(logger.isDebugEnabled()) {
    				logger.debug("received pong");
    			}
        		//noting to do
        		return;
        	}
            release = false;
            ctx.fireChannelRead(msg);
        } finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ReadTimeoutException || cause instanceof IOException) {
			ctx.channel().close();
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		Channel channel = ctx.channel();
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent)evt;
			if(logger.isDebugEnabled()) {
				logger.debug(e.state().name());
			}
			switch (e.state()) {
				case READER_IDLE:
				case ALL_IDLE:
					//读写空闲，关闭连接
					//读空闲，关闭连接
					channel.close();
					if(logger.isInfoEnabled()) {
						logger.info(channel.toString()+", state="+e.state().name()+" then close it");
					}
					break;
				case WRITER_IDLE:
					//写空闲，发送心跳
					channel.write(heartbeatData.ping());
					break;
			}
		}
	}
}
