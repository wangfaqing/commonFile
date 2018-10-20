package org.vps.service.proxy.http;


import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.util.keyValue;
import org.vps.service.proxy.AuthorizationInfo;
import org.vps.service.proxy.AuthorizationParse;

import jodd.util.Base64;

public class HttpProxyHeaderParse {
    public static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyHeaderParse.class);
    
    private static final String AUTHORIZATION_PRO_BASIC       = "Basic";
    
    private static final String HEADER_METHOD_IP = "ip";
    private static final String HEADER_METHOD_PROXY_AUTHORIZATION = "proxy-authorization";
    @SuppressWarnings("unused")
    private static final String HEADER_METHOD_USER_AGENT = "user-agent";
    
    public static void parse(HttpProxyAdapter httpProxyAdpter) {
        List<keyValue<String, String>> headers =  httpProxyAdpter.getRequestHeader().getHeaders();
        Iterator<keyValue<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            keyValue<String, String> header = iterator.next();
            String key = header.getKey().toLowerCase();
            try {
                switch (key) {
                case HEADER_METHOD_IP:
                    parseIP(httpProxyAdpter, iterator, header.getKey(), header.getValue());
                    break;
                case HEADER_METHOD_PROXY_AUTHORIZATION:
                    parseUserProxyAuthorization(httpProxyAdpter, iterator, header.getKey(), header.getValue());
                    break;
                default:
                    break;
                }    
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
    
    private static void parseIP(HttpProxyAdapter proxySession, Iterator<keyValue<String, String>> iterator, String key, String value) {
        proxySession.getLogProxySession().setIpHeader(value);
    }
    
    @SuppressWarnings("unused")
    private static void parseUserAgent(HttpProxyAdapter proxySession, Iterator<keyValue<String, String>> iterator, keyValue<String, String> header, String key, String value) {
        String[] values = value.split(" ");
        if (values.length > 0 && values[values.length-1].length() == 32) {
            String ipMd5 = values[values.length-1];
            if (ipMd5.indexOf("/") == -1 && ipMd5.indexOf(")") == -1) {
                proxySession.getLogProxySession().setIpMD5Header(ipMd5);
                if (values.length == 1) {
                    iterator.remove();
                } else {
                    if (value.length() >= 33) {
                        header.setValue(value.substring(0, value.length() - 33));
                    } else {
                        LOGGER.warn("value length < 33, key: {}, value: {}", key, value);
                    }
                }
            }
        }
    }
    
    private static void parseUserProxyAuthorization(HttpProxyAdapter proxySession, Iterator<keyValue<String, String>> iterator, String key, String value) {
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
        
        AuthorizationInfo info = AuthorizationParse.parse(account, pwd);
        proxySession.setProxyAuthorizationInfo(info);
        LogProxySession logProxySession = proxySession.getLogProxySession();
        if (logProxySession.getHasAUZ() == 0 && info != null) {
            logProxySession.setAUZInfo(account + ":" + pwd);
            logProxySession.setUser(info.getUsername());
            logProxySession.setDid(info.getDid());
            logProxySession.setUid(info.getUid());
            logProxySession.setPid(info.getArea());
            logProxySession.setCid(info.getCid());
            logProxySession.setMark(info.getMark());
            logProxySession.setIp(info.getIp());
            logProxySession.setSip(info.getSip());
            logProxySession.setDip(info.getDip());
            logProxySession.setIpl(info.getIpl());
            logProxySession.setIpmuc(info.getIpmuc());
            logProxySession.setBase(info.getBase());
            // 标记已经有鉴权信息
            logProxySession.setHasAUZ(1);
        }
    }
    
}
