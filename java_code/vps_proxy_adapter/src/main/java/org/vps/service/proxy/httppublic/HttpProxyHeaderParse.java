package org.vps.service.proxy.httppublic;


import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.util.keyValue;
import org.vps.service.proxy.AuthorizationParse;
import org.vps.service.proxy.PublicProxyParam;

import jodd.util.Base64;

public class HttpProxyHeaderParse {
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyHeaderParse.class);
    
    private static final String AUTHORIZATION_PRO_BASIC       = "Basic";
    
    private static final String HEADER_METHOD_PROXY_AUTHORIZATION = "proxy-authorization";
    
    public static void parse(HttpProxyAdapter httpProxyAdpter) {
        List<keyValue<String, String>> headers =  httpProxyAdpter.getRequestHeader().getHeaders();
        Iterator<keyValue<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            keyValue<String, String> header = iterator.next();
            String key = header.getKey().toLowerCase();
            try {
                if (key.equals(HEADER_METHOD_PROXY_AUTHORIZATION)) {
                    parseUserProxyAuthorization(httpProxyAdpter, iterator, header.getKey(), header.getValue());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
    
    private static void parseUserProxyAuthorization(HttpProxyAdapter proxySession, Iterator<keyValue<String, String>> iterator, String key, String value) {
        try {
            String[] p = value.split(" ");
            if (p.length != 2 || !p[0].equals(AUTHORIZATION_PRO_BASIC)) return ;
            
            String usernameAndPwd = Base64.decodeToString(p[1]);
            if (usernameAndPwd == null) return ;
            proxySession.getLogProxySession().setAUZInfo(usernameAndPwd);
            
            String account = "", pwd = "";
            String[] p1 = usernameAndPwd.split(":");
            if (p1.length >= 2) {
                account = p1[0];
                pwd = p1[1];
            }
            //proxyParam
            PublicProxyParam proxyParam = AuthorizationParse.parsePublic(account, pwd);
            proxySession.setProxyParam(proxyParam);
            LogProxySession logProxySession = proxySession.getLogProxySession();
            if (logProxySession.getHasAUZ() == 0 && proxyParam != null) {
                logProxySession.setAUZInfo(account + ":" + pwd);
                logProxySession.setUser(proxyParam.getUser());
                logProxySession.setUid(proxyParam.getUid());
                logProxySession.setSip(proxyParam.getSip());
                logProxySession.setDid(Integer.toString(proxyParam.getRid()));
                logProxySession.setPid(proxyParam.getPid());
                // 标记已经有鉴权信息
                logProxySession.setHasAUZ(1);
            }
        } finally {
            // 解析无论失败，也把header line 删除
            iterator.remove();
        }
    }
    
}
