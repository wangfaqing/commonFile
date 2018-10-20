package org.vps.service.proxy.httppublic.ordercheck;


import edu.hziee.cap.common.util.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.util.Util;
import org.vps.service.proxy.ProxyStaticVar;
import org.vps.service.proxy.httppublic.ordercheck.bean.PublicProxyParam;

import java.util.Date;

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

    private static boolean signCheck(PublicProxyParam info) {
        String context = String.format("orderId=%s&secret=%s&time=%s",info.getOrderId(),info.getSecret(),info.getTime());

        String sign = Md5Utils.md5Hex(context).toLowerCase();

        if (!sign.equals(info.getSign())) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("sign not equals. orderId: {}, sectet: {}, time:{}, sign: {}, server sign: {}", new Object[]{
                        info.getOrderId(), info.getSecret(), Long.toString(info.getTime()), info.getSign(), sign});
            }
            info.setErrorMsg("605 Sign Is Error");
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
            // 服务器时间-1小时 <= 请求方时间  <= 服务器时间+1小时 允许访问
            long now = new Date().getTime() / 1000;
            long begin = now - 3600;
            long end = now + 3600;
            if (proxyParam.getTime() < begin || proxyParam.getTime() > end) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Proxy param time fail. orderId: {},  time: {}, server time: {}", new Object[]{
                            proxyParam.getOrderId(),proxyParam.getTime(), now});
                }
                proxyParam.setErrorMsg("604 Sign Time Out");
                return false;
            }    
        }
        
        if (!signCheck(proxyParam)) {
            return false;
        }
        
        return true;
    }
    

    
}
