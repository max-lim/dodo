package org.dodo.rpc;

import org.dodo.rpc.remoting.Message;
import org.dodo.rpc.remoting.ServerHandler;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 服务端handler
 * @author maxlim
 *
 */
public class RpcServerHandler implements ServerHandler {
	BiConsumer<Message, Consumer> requestConsumer;
	public RpcServerHandler(BiConsumer<Message, Consumer> requestConsumer) {
		this.requestConsumer = requestConsumer;
	}

	@Override
	public void onReceive(Message msg, Consumer<Message> responseMessageConsumer) {
		this.requestConsumer.accept(msg, responseMessageConsumer);
	}
}
