package org.vps.service.proxy;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.util.StringTools;

import edu.hziee.common.lang.StringUtil;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.QueryStringDecoder;

public class AuthorizationParse {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationParse.class);
    
    private static String AUTHORIZATION_PWD_KEY_DID     = "did";
    private static String AUTHORIZATION_PWD_KEY_UID     = "uid";
    private static String AUTHORIZATION_PWD_KEY_SID     = "sid";
    private static String AUTHORIZATION_PWD_KEY_PID     = "pid";
    private static String AUTHORIZATION_PWD_KEY_CID     = "cid";
    private static String AUTHORIZATION_PWD_KEY_SIP     = "sip";
    private static String AUTHORIZATION_PWD_KEY_DIP     = "dip";
    private static String AUTHORIZATION_PWD_KEY_IP      = "ip";
    private static String AUTHORIZATION_PWD_KEY_UUID    = "uuid";
    @SuppressWarnings("unused")
    private static String AUTHORIZATION_PWD_KEY_MOD     = "mod";
    private static String AUTHORIZATION_PWD_KEY_IPL     = "ipl";
    private static String AUTHORIZATION_PWD_KEY_IPMUC   = "ipmuc";
    private static String AUTHORIZATION_PWD_KEY_BASE    = "base";
    private static String AUTHORIZATION_PWD_KEY_MARK    = "mark";
    private static String AUTHORIZATION_PWD_KEY_SIGN    = "sign";
    private static String AUTHORIZATION_PWD_KEY_T       = "t";
    
    private static String AUTHORIZATION_PWD_KEY_PUBLIC_RID  = "rid";
    private static String AUTHORIZATION_PWD_KEY_PUBLIC_T    = "t";
    private static String AUTHORIZATION_PWD_KEY_PUBLIC_SIGN = "sign";
    private static String AUTHORIZATION_PWD_KEY_PUBLIC_UID  = "uid";
    private static String AUTHORIZATION_PWD_KEY_PUBLIC_SIP  = "sip";
    private static String AUTHORIZATION_PWD_KEY_PUBLIC_PID  = "pid";
    
    public static AuthorizationInfo parse(String account, String password) {
        if (StringUtil.isBlank(account) || StringUtil.isBlank(password)) return null;

        AuthorizationInfo info = new AuthorizationInfo();
        info.setUsername(account);
        
        QueryStringDecoder decoderQuery = new QueryStringDecoder(password, HttpConstants.DEFAULT_CHARSET, false, 15);
        Map<String, List<String>> keyMap = decoderQuery.parameters();
        info.setDid(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_DID));
        info.setUid(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_UID));
        info.setPid(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PID), -1));
        info.setCid(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_CID), -1));
        info.setUuid(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_UUID));
        info.setT(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_T), 0));
        info.setSign(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_SIGN));
        
        int sid = StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_SID), -1);
        if (sid != -1) {
            info.setSid(sid);
        }
        info.setSip(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_SIP), -1));
        info.setDip(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_DIP), -1));
        info.setIp(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_IP));
        
        info.setIpl(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_IPL), 0));
        info.setIpmuc(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_IPMUC), 0));
        info.setBase(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_BASE));
        
        info.setMark(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_MARK));
        return info;
    }
    
    public static PublicProxyParam parsePublic(String account, String password) {
        if (StringUtil.isBlank(account) || StringUtil.isBlank(password)) return null;

        PublicProxyParam info = new PublicProxyParam();
        info.setUser(account);
        
        QueryStringDecoder decoderQuery = new QueryStringDecoder(password, HttpConstants.DEFAULT_CHARSET, false, 15);
        Map<String, List<String>> keyMap = decoderQuery.parameters();
        
        
        info.setRid(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PUBLIC_RID), -1));
        info.setT(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PUBLIC_T), 0));
        info.setSign(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PUBLIC_SIGN));
        info.setUid(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PUBLIC_UID));
        if (info.getUid().length() > 32) info.setUid(info.getUid().substring(0, 32));
        info.setPid(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PUBLIC_PID), -1));
        info.setSip(StringTools.String2Int(getValueFromAuthorizationPwd(keyMap, AUTHORIZATION_PWD_KEY_PUBLIC_SIP), 1));
        
        return info;
    }
    
    private static String getValueFromAuthorizationPwd(Map<String, List<String>> keyMap, String key) {
        List<String> value = keyMap.get(key);
        if (value == null || value.isEmpty()) return "";
        return value.get(0);
    }   
}
