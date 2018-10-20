package org.vps.app.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis客户端操作类
 * @author wangyaofeng
 *
 */
public class ProxyTransferTb {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyTransferTb.class);
    
	// redis连接池
	private static JedisPool jedisPool = null;
	
	public static final int TIME_OUT = 60 * 60 * 3;    //3小时
	
	/**
     * @param jedisPool the jedisPool to set
     */

    public static void setJedisPool(JedisPool jedisPool) {
        ProxyTransferTb.jedisPool = jedisPool;
    }
	
	/**
	 * 获取redis连接
	 * @return
	 */
	private static Jedis getJedis() {
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}
	
	/**
	 * 
	 * get: 获取key的计数
	 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
	 * @param key
	 * @return
	 */
	public static String get(String key) {
	    try {
            Jedis jedis = getJedis();
            try {
                return jedis.get(key);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
	    return null;
	}
	
	/**
	 * 
	 * set: 写redis
	 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
	 * @param key
	 * @param value
	 * @param timeout
	 */
	public static void set(String key, String value, int timeout) {
        try {
            Jedis jedis = getJedis();
            try {
                jedis.setex(key, timeout, value);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
