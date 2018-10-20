package org.vps.service.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.OrderInfo;
import org.vps.app.redis.RedisClient;
import org.vps.app.util.DateTools;

public class PublicOrderInfo {

    public static final Logger LOGGER = LoggerFactory.getLogger(PublicOrderInfo.class);

    public static final int TIME_OUT = 60 * 60 * 24;    //24小时

    public static final Long DAY_LIMIT_FLOW = new Double(4.5 * 1024 * 1024 * 1024).longValue();    //4.5G流量

    private OrderInfo orderInfo;

    /* 并发数 */
    private volatile int connCount;

    /* 订单更新时间 */
    private volatile long lastUpdate = 0;

    public PublicOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
        lastUpdate = System.currentTimeMillis();
    }

    public PublicOrderInfo() {
    }

    /**
     * 校验订单信息
     * @return
     */
    public boolean checkOrderInfo() {
        if(orderInfo == null) {
            LOGGER.warn("无订单信息");
            return false;
        }
        if(getConn() >= orderInfo.getConcurrentUsers()) {
            LOGGER.warn("并发数已达上限！！！");
            return false;
        }
        String flowKey = orderInfo.getOrderId() + "_flow_" + DateTools.getDate();
        Long todayUsedFlow = Long.valueOf(RedisClient.get(flowKey));
        if(todayUsedFlow == null) {
            todayUsedFlow = 0L;
            RedisClient.set(flowKey, todayUsedFlow.toString(), TIME_OUT);
        }
        if(todayUsedFlow >= DAY_LIMIT_FLOW) {
            LOGGER.warn("流量已达上限！！！");
            return false;
        }
        return true;
    }

    public void updateFlow(Long flow) {
        String flowKey = orderInfo.getOrderId() + "_flow_" + DateTools.getDate();
        RedisClient.incre(flowKey, flow);
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * 校验并发数
     * @return
     */
    public boolean isConnLimit() {
        if (connCount >= orderInfo.getConcurrentUsers()) {
            return false;
        }
        return true;
    }

    public int getConn() {
        return connCount;
    }

    public void inrConn() {
        connCount++;
    }

    public void decrConn() {
        connCount--;
    }

}
