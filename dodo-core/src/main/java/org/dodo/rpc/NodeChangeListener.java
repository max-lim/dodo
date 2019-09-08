package org.dodo.rpc;

/**
 * 节点变化监听器
 * @author maxlim
 *
 */
public interface NodeChangeListener {
    /**
     * 新增节点
     * @param service
     * @param node
     */
    public void onNodeAdd(String service, Node node);

    /**
     * 触发节点删除（网络断开）
     * @param node
     */
    public void noNodeRemoved(String service, Node node);
}
