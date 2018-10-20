package org.vps.app.log.dao.mapper;

import org.vps.app.log.dao.modle.LogProxySession;

public interface ProxySessionLogMapper  {

	// 代理使用日志，相关数据插入数据库
	int insertProxySessionLog(LogProxySession logProxySession);
	
}
