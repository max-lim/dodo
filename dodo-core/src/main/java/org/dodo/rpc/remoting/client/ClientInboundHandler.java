package org.dodo.rpc.remoting.client;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dodo.rpc.remoting.HeartbeatData;
import org.dodo.rpc.remoting.ClientListener;
import org.dodo.rpc.remoting.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ClientInboundHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(ClientInboundHandler.class);
    private ClientListener statusHandler;
    private HeartbeatData<Message> heartbeatData;

    public ClientInboundHandler(ClientListener statusHandler, HeartbeatData heartbeatData) {
        this.statusHandler = statusHandler;
        this.heartbeatData = heartbeatData;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelActive(ctx);
        } finally {
            this.statusHandler.onConnect();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelInactive(ctx);
        } finally {
            ctx.close();
            this.statusHandler.onClose();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if(heartbeatData.isPing(msg)) {
            if(logger.isDebugEnabled()) {
                logger.debug("{} received ping", ctx.channel().toString());
            }
            ctx.channel().writeAndFlush(heartbeatData.pong(msg));
        } else if(heartbeatData.isPong(msg) || msg.getType() == Message.Command.RESPONSE) {// || msg.getType() == Message.Command.ERROR
            if(logger.isTraceEnabled()) {
                logger.trace("{} received response", msg.toString());
            }
            ResponseCallback callback = ClientContext.context().removeCallback(msg.getSeq());
            if(callback != null) {
                callback.callback(msg);
            } else {
                RequestFuture future = ClientContext.context().removeFuture(msg.getSeq());
                if(future != null) {
                    future.set(msg);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error(ctx.channel().toString(), cause);
    }
}