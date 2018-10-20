package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;

public class AuthorizationCheckForSocks5 {
    public static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCheckForSocks5.class);
    
    public static boolean isPass(LogProxySession logProxySession, AuthorizationInfo _info) {
        AuthorizationCheckInfo info = new AuthorizationCheckInfo(
                0, 
                _info, 
                true, 
                null, 
                null, 
                logProxySession);
        
        info.setTimeCheck(false);
        info.setUuidCheck(false);
        
        return AuthorizationCheck.isPass(info);
    }
    
}
