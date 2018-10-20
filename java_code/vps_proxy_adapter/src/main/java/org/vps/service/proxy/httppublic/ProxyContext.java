package org.vps.service.proxy.httppublic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.log.dao.modle.PublicUser;
import org.vps.service.proxy.AuthorizationInfo;

/*
 * 代理上下文基类
 */
public class ProxyContext {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyContext.class);
    
    private LogProxySession logProxySession;
    private AuthorizationInfo info;
    private PublicUser user;
//    private Params params;
    
    public ProxyContext() {
        
    }

    public LogProxySession getLogProxySession() {
        return logProxySession;
    }

    public void setLogProxySession(LogProxySession logProxySession) {
        this.logProxySession = logProxySession;
    }

    public AuthorizationInfo getInfo() {
        return info;
    }

    public void setInfo(AuthorizationInfo info) {
        this.info = info;
    }

    public PublicUser getUser() {
        return user;
    }

    public void setUser(PublicUser user) {
        this.user = user;
    }
    
    
}
