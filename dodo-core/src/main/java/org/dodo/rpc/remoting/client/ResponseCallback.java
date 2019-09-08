package org.dodo.rpc.remoting.client;

import org.dodo.rpc.remoting.Message;

/**
 * 异步响应回调接口
 * @author maxlim
 *
 */
public interface ResponseCallback {
	public void callback(Message message);
}
