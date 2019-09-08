package org.dodo.rpc.remoting;

/**
 * 客户端状态监听接口
 * @author maxlim
 *
 */
public interface ClientListener {

    void onConnect();

    void onClose();


}

