package org.dodo.rpc.remoting;

import java.util.function.Consumer;

/**
 * 服务端网络事件handler
 * @author maxlim
 *
 */
public interface ServerHandler {
    public void onReceive(final Message msg, final Consumer<Message> responseMessageConsumer);
}
