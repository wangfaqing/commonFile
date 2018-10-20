package org.vps.app.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);

    public static final int TIME_OUT = 60 * 60 * 3;    //3小时

    // redis连接池
    private static JedisPool jedisPool = null;

    public static void setJedisPool(JedisPool jedisPool) {
        RedisClient.jedisPool = jedisPool;
    }

    /**
     * 获取redis连接
     * @return
     */
    private static Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    public static void setObj(String key, Object obj, int time) {
        try {
            Jedis jedis = getJedis();
            try {
                jedis.setex(key, time, JSONObject.toJSONString(obj));
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static Object getObj(String key,Class clazz) {
        try {
            Jedis jedis = getJedis();
            try {
                return JSON.parseObject(jedis.get(key),clazz);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static void set(String key, String value) {
        try {
            Jedis jedis = getJedis();
            try {
                jedis.set(key, value);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

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

    public static Long incre(String key, long value) {
        try {
            Jedis jedis = getJedis();
            try {
                return jedis.incrBy(key, value);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static void expire(String key, int seconds) {
        try {
            Jedis jedis = getJedis();
            try {
                jedis.expire(key, seconds);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
