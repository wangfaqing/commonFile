package org.vps.app.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * ip使用计数操作类
 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
 * @version 1.0.0
 */
public class ProxyTransferRule {
    @SuppressWarnings("unused")
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyTransferRule.class);
	
    private final static String PROFIX = "PTR-";
	/**
	 * 
	 * get: 获取base+ip的计数
	 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
	 * @param key
	 * @param ip
	 * @return
	 */
	public static String get(String name, String uid) {
	    String key = PROFIX + name + "_" + uid;
	    return ProxyTransferTb.get(key);
	}
	
	/**
	 * 
	 * incr: 自增base+ip的使用数
	 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
	 * @param base
	 * @param ip
	 */
    public static void set(String name, String uid, String value) {
        String key = PROFIX + name + "_" + uid;
        ProxyTransferTb.set(key, value, ProxyTransferTb.TIME_OUT);
    }

}
