package org.vps.app.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vps.app.log.dao.mapper.ProxySessionLogMapper;
import org.vps.app.log.dao.modle.LogProxySession;

import edu.hziee.common.queue.IBatchExecutor;

public class ProxySessionLogExecution implements IBatchExecutor<LogProxySession> {

    private static final Logger logger = LoggerFactory.getLogger(ProxySessionLogExecution.class);
    
	@Autowired
	private ProxySessionLogMapper proxySessionMapper;
	
	@Override
	public void execute(List<LogProxySession> list) {
		for (LogProxySession item : list ) {
		    try {
		        proxySessionMapper.insertProxySessionLog(item);    
            } catch (Exception e) {
                if (logger.isErrorEnabled())
                    logger.error("入库", e);
            }		    
		}
	}
}
