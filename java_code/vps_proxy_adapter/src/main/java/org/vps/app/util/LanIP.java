package org.vps.app.util;


import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.cap.common.util.IpUtils;

public class LanIP {
    public static final Logger LOGGER = LoggerFactory.getLogger(LanIP.class);
    
    private static final LanIP INSTANCE = new LanIP();
    
    private ArrayList<keyValue<Long, Long>> ipLangPair = new ArrayList<>();
    private keyValue<Long, Long> aliyumIpPair;
    
    private LanIP() {
        init();
    }
    
    private void init() {
        ipLangPair.add(new keyValue<Long, Long>(
                new Long(IpUtils.ip2Long("10.0.0.0")), 
                new Long(IpUtils.ip2Long("10.255.255.255"))));
        
        ipLangPair.add(new keyValue<Long, Long>(
                new Long(IpUtils.ip2Long("172.16.0.0")), 
                new Long(IpUtils.ip2Long("172.31.255.255"))));
        
        ipLangPair.add(new keyValue<Long, Long>(
                new Long(IpUtils.ip2Long("192.168.0.0")), 
                new Long(IpUtils.ip2Long("192.168.255.255"))));
        
        ipLangPair.add(new keyValue<Long, Long>(
                new Long(IpUtils.ip2Long("100.64.0.0")), 
                new Long(IpUtils.ip2Long("100.127.255.255"))));
        
        aliyumIpPair = new keyValue<Long, Long>(
                new Long(IpUtils.ip2Long("100.64.0.0")), 
                new Long(IpUtils.ip2Long("100.127.255.255")));
    } 
  
    public static boolean isLanIp(String host) {
        try {
            long lHost = IpUtils.ip2Long(host);
            for(keyValue<Long, Long> pair : INSTANCE.ipLangPair) {
                if (lHost <= pair.getValue() && lHost >= pair.getKey()) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
    
    public static boolean isAliyumIp(String host) {
        try {
            long lHost = IpUtils.ip2Long(host);
            if (lHost <= INSTANCE.aliyumIpPair.getValue() && lHost >= INSTANCE.aliyumIpPair.getKey()) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
    
}
