package org.dodo.rpc.remoting;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码
 * @author maxlim
 *
 */
public class Decoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readableBytes = in.readableBytes();
		if(readableBytes >= Message.HEADER_LENGTH) {
			in.markReaderIndex();
			int length = in.readInt();
			if(readableBytes >= length) {
				Message msg = new Message();
				msg.setLength(length);
				msg.setVersion(in.readByte());
				//version 1...
				msg.setType(Message.Command.getType(in.readByte()));
				msg.setSeq(in.readLong());
				byte[] body = new byte[length - Message.HEADER_LENGTH];
				in.readBytes(body);
				msg.setBody(body);
				out.add(msg);
			}
			else {
				in.resetReaderIndex();
			}
		}
	}

}
