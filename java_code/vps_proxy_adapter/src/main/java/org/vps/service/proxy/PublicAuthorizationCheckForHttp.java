package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;

public class PublicAuthorizationCheckForHttp {
    public static final Logger LOGGER = LoggerFactory.getLogger(PublicAuthorizationCheckForHttp.class);
    
    public static boolean isPass(PublicProxyParam proxyParam, LogProxySession logProxySession) {
        PublicAuthorizationCheckInfo info = new PublicAuthorizationCheckInfo(
                0, 
                proxyParam, 
                true, 
                null, 
                null, 
                logProxySession);
        info.setTimeCheck(true);
        return PublicAuthorizationCheck.isPass(info);
    }
    
}
