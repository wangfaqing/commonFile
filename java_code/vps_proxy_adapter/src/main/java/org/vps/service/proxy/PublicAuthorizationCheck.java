package org.vps.service.proxy;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.cms.AppInstance;
import org.vps.app.log.dao.modle.PublicUser;
import org.vps.app.util.Util;

import edu.hziee.cap.common.util.Md5Utils;

public class PublicAuthorizationCheck {
    public static final Logger LOGGER = LoggerFactory.getLogger(PublicAuthorizationCheck.class);
    
    public static boolean isPass(PublicAuthorizationCheckInfo info) {
        boolean ret = true;
        // 代理鉴权
        info.getLogProxySession().setUserType(ProxyStaticVar.USER_TYPE_OTHER);
        info.getLogProxySession().setPass(ProxyStaticVar.UNPASS);
        if (info.isAuthorizationEnable()) {
            ret = false;
            if (isAuthentication(info)) {
                info.getLogProxySession().setPass(info.getPass());
                ret = true;
            }
        } else {
            info.getLogProxySession().setPass(ProxyStaticVar.PASS_AUZ_UNABLE);
        }
        
        // 白名单
//        if (isInWhiteList(info)) {
//            info.getLogProxySession().setUserType(ProxyStaticVar.USER_TYPE_WHITELIST);
//            info.getLogProxySession().setPass(ProxyStaticVar.PASS_WHITELIST);
//            ret = true;
//        }
        
        // 黑名单
//        if (isInBlackList(info)) {
//            info.getLogProxySession().setUserType(ProxyStaticVar.USER_TYPE_BLACKLIST);
//            info.getLogProxySession().setPass(ProxyStaticVar.UNPASS);
//            if (LOGGER.isWarnEnabled()) {
//                LOGGER.warn("ip in blacklist. ip: {}", info.getLogProxySession().getIpRequest());
//            }
//            ret = false;
//        }
        
        return ret;
    }

    public static boolean isInWhiteList(PublicAuthorizationCheckInfo info) {
        String ipSession = Util.getIp(info.getLogProxySession().getIpRequest().substring(1));
        for (String ip : info.getIpWhiteList()) {
            if (ip.equals(ipSession)) {
                return true;
            }
        }
        return false;
    } 
    
    public static boolean isInBlackList(PublicAuthorizationCheckInfo info) {
        String ipSession = Util.getIp(info.getLogProxySession().getIpRequest().substring(1));
        for (String ip : info.getIpBlackList()) {
            if (ip.equals(ipSession)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean signCheck(PublicProxyParam info, boolean uuidUsed) {
        String context = String.format("rid=%s&t=%s&key=%s", info.getRid(), info.getT(), getKey(info.getUser()));
 
        String sign = Md5Utils.md5Hex(context).toLowerCase();
        
        if (!sign.equals(info.getSign())) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("sign not equals. user: {}, rid: {}, t:{}, sign: {}, server sign: {}", new Object[]{
                        info.getUser(), info.getRid(), info.getT(), info.getSign(), sign});
            }
            return false;
        }
        return true;
    }
    
    public static boolean isAuthentication(PublicAuthorizationCheckInfo info) {
        PublicProxyParam proxyParam = info.getInfo();
        if (proxyParam == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Proxy param is null. url: {}", info.getLogProxySession().getUrl());
            }
            return false;
        }
        
        if (info.isTimeCheck()) {
            // 服务器时间-24小时 <= 请求方时间  <= 服务器时间+1小时 允许访问
            long now = new Date().getTime() / 1000;
            long begin = now - 3600 * 24;
            long end = now + 3600;
            if (proxyParam.getT() < begin && proxyParam.getT() > end) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Proxy param time fail. user: {}, rid: {}, t: {}, server t: {}", new Object[]{
                            proxyParam.getUser(), proxyParam.getRid(), proxyParam.getT(), now});
                }
                return false;
            }    
        }
        
        if (!signCheck(info.getInfo(), info.isUuidCheck())) {
            return false;
        }
        
        return true;
    }
    
    private static String getKey(String username) {
        PublicUser user = AppInstance.getPublicUsers().getPublicUser(username);
        if (user != null) return user.getKey();
        return "";
    }
    
}
