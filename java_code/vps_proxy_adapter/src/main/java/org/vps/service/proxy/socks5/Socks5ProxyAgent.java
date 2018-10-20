package org.vps.service.proxy.socks5;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.service.mgr.VpsCMS;
import org.vps.service.proxy.AuthorizationInfo;

public class Socks5ProxyAgent {
    public static final Logger LOGGER = LoggerFactory.getLogger(Socks5ProxyAgent.class);
    
    private LogProxySession logProxySession;
    private AuthorizationInfo authorizationInfo;
    
    public Socks5ProxyAgent(ISocks5ProxyHandler handler) {
        logProxySession = new LogProxySession(VpsCMS.getConfig().getVpsLocalIP(), handler.socks5GetRemoteAddr());
        logProxySession.setConnectTimestamp(new Date().getTime());    
    }
    
    
    /**
     * @return the logProxySession
     */
    public LogProxySession getLogProxySession() {
        return logProxySession;
    }

    /**
     * @return the authorizationInfo
     */
    public AuthorizationInfo getAuthorizationInfo() {
        return authorizationInfo;
    }
    
}
