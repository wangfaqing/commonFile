package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.service.proxy.http.HttpProxyAdapter;

public class AuthorizationCheckForHttp {
    public static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCheckForHttp.class);
    
    public static boolean isPass(HttpProxyAdapter proxySession) {
        AuthorizationCheckInfo info = new AuthorizationCheckInfo(
                0, 
                proxySession.getProxyAuthorizationInfo(), 
                true, 
                null, 
                null, 
                proxySession.getLogProxySession());
        
        return AuthorizationCheck.isPass(info);
    }

    public static boolean isPass(org.vps.service.proxy.httppublic.HttpProxyAdapter httpProxyAdapter) {
        AuthorizationCheckInfo info = new AuthorizationCheckInfo(
                0, 
                httpProxyAdapter.getAuthorizationInfo(),
                true, 
                null, 
                null, 
                httpProxyAdapter.getLogProxySession());
        
        return AuthorizationCheck.isPass(info);
    }
    
}
