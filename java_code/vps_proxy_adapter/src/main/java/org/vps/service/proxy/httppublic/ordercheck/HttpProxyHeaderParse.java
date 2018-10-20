package org.vps.service.proxy.httppublic.ordercheck;


import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.QueryStringDecoder;
import jodd.util.Base64;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.util.keyValue;
import org.vps.service.proxy.httppublic.ordercheck.bean.PublicProxyParam;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
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
                    parseUserProxyAuthorization(httpProxyAdpter, header.getValue());
                    iterator.remove();
                    break;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
    
    private static void parseUserProxyAuthorization(HttpProxyAdapter proxySession, String value) {
        String[] p = value.split(" ");
        if (p.length != 2 || !p[0].equals(AUTHORIZATION_PRO_BASIC)) return ;
        if (StringUtil.isBlank(p[1])) {
            LOGGER.warn("Header信息有误");
            return;
        }
        String author = Base64.decodeToString(p[1]);
        PublicProxyParam proxyParam = getValueFromAuthorization(author);
        if (proxyParam == null) {
            return;
        }
        LogProxySession logProxySession = proxySession.getLogProxySession();
        if (logProxySession.getHasAUZ() == 0) {
            proxySession.setProxyParam(proxyParam);
            logProxySession.setHasAUZ(1);
        }
    }


    private static PublicProxyParam getValueFromAuthorization(String author) {
        String account = "", pwd = "";
        String[] p1 = author.split(":");
        if (p1.length >= 2) {
            account = p1[0];
            pwd = p1[1];
        }
        if (!StringUtil.isBlank(account) && !StringUtil.isBlank(pwd)) {
            QueryStringDecoder decoderQuery = new QueryStringDecoder(pwd, HttpConstants.DEFAULT_CHARSET, false, 15);
            Map<String, List<String>> keyMap = decoderQuery.parameters();
            PublicProxyParam proxyParam = new PublicProxyParam();
            proxyParam.setOrderId(getkey(keyMap, "orderId"));
//            proxyParam.setSecret(getkey(keyMap,"secret"));
            proxyParam.setTime(Long.parseLong(getkey(keyMap, "time")));
            proxyParam.setSign(getkey(keyMap,"sign"));
            return proxyParam;
        }else{
            LOGGER.warn("proxy-authorization错误:   [accout:{}],[pwd:{}]",account,pwd);
        }
        return null;

    }

    private static String getkey(Map<String, List<String>> keyMap,String key) {
        List<String> value = keyMap.get(key);
        if (value == null || value.isEmpty()) return "";
        return value.get(0);
    }

}
