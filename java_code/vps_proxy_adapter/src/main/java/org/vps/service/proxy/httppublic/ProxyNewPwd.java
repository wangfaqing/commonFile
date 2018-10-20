package org.vps.service.proxy.httppublic;


import java.util.Date;
import java.util.UUID;

import org.apache.catalina.util.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.cap.common.util.Md5Utils;
import edu.hziee.common.lang.StringUtil;

public class ProxyNewPwd {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProxyNewPwd.class);
    
    public static String createSocks5ProxyPwd(String key, String did, String uid, int pid, int cid) {
        long timestamp = new Date().getTime() / 1000;
        String pwd = String.format("did=%s&uid=%s&pid=%d&cid=%d&t=%d", did, uid, pid, cid, timestamp);
        String context = String.format("did=%s&uid=%s&pid=%d&cid=%d&t=%d&key=%s", did, uid, pid, cid, timestamp, key);
        String sign = Md5Utils.md5Hex(context).toLowerCase();
        pwd += "&sign=" + sign;
        return pwd;
    }
    
    public static String createSocks5ProxyPwdex(String key, String did, String uid, int pid, int cid, int sip, int dip, int mod, String ip) {
        String pwdBase = createSocks5ProxyPwd(key, did, uid, pid, cid);
        if (sip != -1) pwdBase += "&sip=" + Integer.toString(sip);
        if (dip != -1) pwdBase += "&dip=" + Integer.toString(dip);
        if (mod != -1) pwdBase += "&mod=" + Integer.toString(mod);
        if (!StringUtil.isBlank(ip)) pwdBase += "&ip=" + new URLEncoder().encode(ip); 


        return pwdBase;
    }
    
    private static String createProxyPwd(String key, String did, String uid, int pid, int cid) {
        String uuid = UUID.randomUUID().toString().toLowerCase();
        long timestamp = new Date().getTime() / 1000;
        String pwd = String.format("did=%s&uid=%s&pid=%d&cid=%d&uuid=%s&t=%d", did, uid, pid, cid, uuid, timestamp);
        String context = String.format("did=%s&uid=%s&pid=%d&cid=%d&uuid=%s&t=%d&key=%s", did, uid, pid, cid, uuid, timestamp, key);
        System.out.println(context);
        String sign = Md5Utils.md5Hex(context).toLowerCase();
        pwd += "&sign=" + sign;
        return pwd;
    }
    
    private static String createProxyPwdex(String key, String did, String uid, int pid, int cid, int sip, int dip, int mod, String ip) {
        String pwdBase = createProxyPwd(key, did, uid, pid, cid);
        if (sip != -1) pwdBase += "&sip=" + Integer.toString(sip);
        if (dip != -1) pwdBase += "&dip=" + Integer.toString(dip);
        if (mod != -1) pwdBase += "&mod=" + Integer.toString(mod);
        if (!StringUtil.isBlank(ip)) pwdBase += "&ip=" + ip;//URLEncoder.encode(ip);
        return pwdBase;
    }
    
    public static String build(String key, String did, String uid, int pid, int sip, int dip) {
        return createProxyPwdex(key, did, uid, pid, -1, sip, dip, -1, null);
    }
}
