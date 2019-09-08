package org.dodo.rpc.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 * @author maxlim
 *
 */
public class Encoder extends MessageToByteEncoder<Message>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getLength());
		out.writeByte(msg.getVersion());
		out.writeByte(msg.getType().get());
		out.writeLong(msg.getSeq());
		
		if(msg.getBody() != null) out.writeBytes(msg.getBody());
	}

}
