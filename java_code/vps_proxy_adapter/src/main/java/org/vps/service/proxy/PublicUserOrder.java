package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.cms.AppInstance;
import org.vps.app.log.dao.modle.OrderUsed;
import org.vps.app.log.dao.modle.PublicUser;
import org.vps.app.redis.Order;
import org.vps.app.redis.ProxyOrderRule;

import edu.hziee.common.lang.StringUtil;

public class PublicUserOrder {
    public static final Logger LOGGER = LoggerFactory.getLogger(PublicUserOrder.class);
    
    private PublicUser user;

    private Order order;
    private OrderUsed orderUsed = new OrderUsed();
    
    private volatile int connCount;
    private volatile long lastUpdate = 0;
//    private volatile boolean update = false;
    
    public PublicUserOrder() {
        lastUpdate = System.currentTimeMillis();
//        update = false;
    }
    
    public PublicUser getUser() {
        return user;
    }

    public void setUser(PublicUser user) {
        this.user = user;
    }
    
    public boolean check() {
        Order orderRest = getOrderRest();
        if (orderRest == null) return false;
        return true;
    }
    
    // 流量 统计
    public void update(long flow) {
        Order orderRest = getOrderRest();
        if (orderRest != null) {
            ProxyOrderRule.update(orderRest.getOrderId(), flow, 1);

            // 超时与数据库同步
            if (StringUtil.isBlank(orderUsed.getOrderId())) {
                orderUsed.setOrderId(orderRest.getOrderId());
            }
            if (!orderRest.getOrderId().equals(orderUsed.getOrderId())) {
                update();
                restOrderUsed(orderRest);
            }
            
            orderUsed.setReq(orderUsed.getReq() + 1);
            orderUsed.setFlow(orderUsed.getFlow() + flow);
            updateEx();
        }
    }
    
    private void restOrderUsed(Order order) {
        orderUsed.setFlow(0);
        orderUsed.setReq(0);
        if (order != null) {
            orderUsed.setOrderId(order.getOrderId());
        }
    }
    
    public boolean isConnLimit() {
        if (connCount >= user.getConLimit()) {
            return true;
        }
        return false;
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
    
    private void updateEx() {
        if (isUpdate()) {
            update();
            restOrderUsed(null);
        }
    }
    
    private boolean isUpdate() {
        // 大于1M or req>100 or updateTime>=60s
        if (orderUsed.getFlow() > 1024*1024 
                || orderUsed.getReq() >= 100 
                || System.currentTimeMillis()- lastUpdate >= 60 * 1000) {
            return true;
        }
        return false;
    }

    private void update() {
        AppInstance.getOrderUsedUpdateMapper().updateOrderUsed(orderUsed);
        lastUpdate = System.currentTimeMillis();
    }
    
    private Order getOrderRest() {
        if (order == null) {
            order = ProxyOrderRule.getOrder(user.getUser());
            if (order == null) return null;
        }
        
        int tryCount = 3;
        do {
            Order orderRest = ProxyOrderRule.getOrderUsed(order.getOrderId());
            if (orderRest != null && orderRest.getFlow() > 0 && orderRest.getReq() > 0) {
                return orderRest;
            } else {
                Order orderNew = ProxyOrderRule.getOrder(user.getUser());
                if (orderNew.getOrderId().equals(order.getOrderId())) {
                    ProxyOrderRule.removeOrder(user.getUser(), order.getOrderId());
                    order = ProxyOrderRule.getOrder(user.getUser());
                } else {
                    order = orderNew;
                }
            }
        } while (0 != tryCount--);
        
        return null;
    }
    
}
