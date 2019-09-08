package org.dodo.register.discovery;

import org.dodo.register.URL;

/**
 * 节点发现接口
 * @author maxlim
 *
 */
public interface DiscoveryListener {
	/**
	 * 监听新增
	 * @param url
	 */
	public void notifyNew(URL url);
	/**
	 * 监听删除
	 * @param url
	 */
	public void notifyRemoved(URL url);
}
