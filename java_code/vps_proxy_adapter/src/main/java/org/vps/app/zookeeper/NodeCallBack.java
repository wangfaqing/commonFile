package org.vps.app.zookeeper;

import java.util.List;

/**
 * 节点变更回调函数
 * @author liyimpc
 *
 */
public interface NodeCallBack {
	
	/**
	 * 更新缓存中的mgr子节点信息
	 */
	void changeCache(List<String> children);
}
