package org.vps.app.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
//import org.vps.app.config.Config;
import org.vps.app.log.dao.modle.LogProxySession;

import com.google.gson.Gson;

import edu.hziee.common.queue.DelayExecuteBuffer;

@Service("logexecution")
public class LogExecution {
    private static final Logger LOGGERPROXYSESSION = LoggerFactory.getLogger("fileProxySessionLog");
    private static final Logger LOGGER = LoggerFactory.getLogger(LogExecution.class);
    
    @Autowired
    @Qualifier("proxySessionLogBuffer")
    private DelayExecuteBuffer<LogProxySession> proxySessionLogQueue ;
    
//    @Autowired
//    private Config config ;
    
    public void Add(LogProxySession proxySession) {
        try {
//            if (config.isLogWriteDB()) {
//            proxySessionLogQueue.add(proxySession);
//            }
//            if (config.isLogWriteFile()) {
            LOGGERPROXYSESSION.info("{}", new Gson().toJson(proxySession));
//            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
