package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.util.LanIP;
import org.vps.app.util.Util;

import edu.hziee.common.lang.StringUtil;

public class AuthorizationCheck {
    public static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCheck.class);
    
    public static boolean isPass(AuthorizationCheckInfo info) {
        boolean ret = false;
        if (info.getInfo() != null && !StringUtil.isBlank(info.getInfo().getUid())) {
//        	info.getLogProxySession().setPass(pass);
            ret = true;
        }
        // 是否是内网机器， 内网优先通过
//        if (isInIntranet(info.getLogProxySession().getIpRequest())) {
//            ret = true;
//        }
        
        return ret;
    }

    public static boolean isInIntranet(String requestRemoteAddress) {
        String ipSession = Util.getIp(requestRemoteAddress.substring(1));
        if (LanIP.isLanIp(ipSession)) return true;
        return false;
    }
    
}
