package org.vps.service.proxy.httppublic.ordercheck.bean;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.cms.AppInstance;
import org.vps.app.log.dao.modle.OrderInfo;
import org.vps.app.redis.RedisClient;
import org.vps.app.util.DateTools;
import java.io.Serializable;
import java.util.Date;

public class OrderStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderStatus.class);

    public static final Logger FLOW_LOGGER = LoggerFactory.getLogger("flowLog");

    private OrderInfo orderInfo;

    /* 并发数 */
    //private volatile int connCount;

    /* 订单更新时间 */
    private volatile long lastUpdate = 0;

    /* 过期时间，24小时 */
    public static final int TIME_OUT = 60 * 60 * 24;

    public OrderStatus(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * 校验订单信息
     * @return
     */
    public boolean checkOrderInfo(PublicProxyParam proxyParam) {
        String secret = proxyParam.getSecret();
        if(StringUtils.isEmpty(secret)) {
            proxyParam.setErrorMsg("609 Secret Is Null");
            return false;
        }
        String orderId = proxyParam.getOrderId();
        String conKey = orderId + "_" + secret;
        Long nowConn = RedisClient.incre(conKey, 1L);
        LOGGER.info("[{}]该订单增加连接数，目前已有连接数[{}]", orderId, nowConn);

        Long expirationDate = orderInfo.getOrderExpiryDate();
        Long nowDate = new Date().getTime();
        if(nowDate >= expirationDate * 1000) {
            LOGGER.warn("[{}]该订单到期，减少一个连接，目前连接数[{}]", orderId, RedisClient.incre(conKey, -1L));
            proxyParam.setErrorMsg("606 Expired Order");
            return false;
        }
        if(nowConn > Long.valueOf(orderInfo.getConcurrentUsers())) {
            LOGGER.warn("[{}]该订单并发数已达上限！！！减少一个连接，目前连接数[{}]", orderId, RedisClient.incre(conKey, -1L));
            proxyParam.setErrorMsg("607 Maximum Number of Concurrency Has Been Reached");
            return false;
        }
        String flowKey = orderId + "_flow_" + DateTools.getDate();
        //获取Redis中流量数
        Long todayUsedFlow = RedisClient.incre(flowKey, 0L);
        if(todayUsedFlow == 0L) {
            LOGGER.info("[{}]该订单暂无使用的流量", orderId);
            RedisClient.expire(flowKey, TIME_OUT);
        }
        if(todayUsedFlow >= orderInfo.getFlowNumber() * 1024 * 1024) {
            LOGGER.warn("[{}]该订单当天流量已达上限！！！减少一个连接，目前连接数[{}]", orderId, RedisClient.incre(conKey, -1L));
            proxyParam.setErrorMsg("608 Day Of Flow Is Limit");
            return false;
        }
        return true;
    }

    /**
     * 更新流量
     * @param flow
     */
    public void updateFlow(Long flow) {
        String flowKey = orderInfo.getOrderId() + "_flow_" + DateTools.getDate();
        Long usedFlow = RedisClient.incre(flowKey, 0L);
        // userFlow 等于0，说明Redis没有 flowKey
        if(usedFlow == 0L){
            RedisClient.expire(flowKey, 24 * 60 * 60);
        }
        usedFlow = RedisClient.incre(flowKey, flow);
        LOGGER.info("[{}]该订单本次使用[{}]B, 今天已使用[{}]B", new Object[]{orderInfo.getOrderId(), flow, usedFlow});
        lastUpdate = System.currentTimeMillis();
        FLOW_LOGGER.info(JSONObject.toJSONString(new FlowBean(orderInfo.getOrderId(), flow, lastUpdate)));
    }

    public void getSecret(PublicProxyParam proxyParam) {
        long userId = orderInfo.getUserId();
        String userSecret = RedisClient.get("User_"+userId);
        if (userSecret == null) {
            userSecret = AppInstance.getUserInfoMapper().getSecretById(userId);
            if (userSecret == null) {
                proxyParam.setErrorMsg("603 User Don't Have Secret");
                return;
            }
            RedisClient.set("User_"+userId , userSecret , 24 * 60 * 60);
        }
        proxyParam.setSecret(userSecret);
    }

    /**
     * 校验并发数
     * @return
     */
//    public boolean isConnLimit() {
//        if (connCount >= orderInfo.getConcurrentUsers()) {
//            return false;
//        }
//        return true;
//    }

//    public int getConnCount() {
//        return connCount;
//    }
//
//    public void inrConn() {
//        connCount++;
//    }
//
//    public void decrConn() {
//        connCount--;
//    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "orderInfo=" + orderInfo +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
