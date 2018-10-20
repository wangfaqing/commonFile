package org.vps.service.proxy.httppublic.ordercheck;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vps.app.cms.AppInstance;
import org.vps.app.log.dao.modle.OrderInfo;
import org.vps.app.redis.RedisClient;
import org.vps.service.proxy.httppublic.ordercheck.bean.OrderStatus;
import org.vps.service.proxy.httppublic.ordercheck.bean.PublicProxyParam;

@Service
public class PublicOrderInfoManage {

    public static final Logger LOGGER = LoggerFactory.getLogger(PublicOrderInfoManage.class);

    /**
     * 校验密钥
     * @param proxyParam
     * @return
     */
    public void getInfo(PublicProxyParam proxyParam) {
        String orderId = proxyParam.getOrderId();
        OrderStatus orderStatus = (OrderStatus)RedisClient.getObj(orderId, OrderStatus.class);
        if (orderStatus == null) {
            LOGGER.info("Redis没有[{}]的订单信息：{}",orderId);
            OrderInfo orderInfo = AppInstance.getOrderInfoMapper().getOrderById(orderId);
            if (orderInfo == null) {
                proxyParam.setErrorMsg("602 Invalid Orders");
                return;
            }
            orderStatus = new OrderStatus(orderInfo);
            int expireTime = 24*60*60;
            LOGGER.info(orderStatus.toString());
            LOGGER.info(JSONObject.toJSONString(orderStatus));
            RedisClient.setObj(proxyParam.getOrderId(), orderStatus, expireTime);
        }
        orderStatus.getSecret(proxyParam);
    }

    /**
     * @author <a href="mailto:wangfaqing@zhexinit.com" >王法清</a>
     * 校验订单
     * @param proxyParam
     * @return
     */
    public boolean checkOrderStatus(PublicProxyParam proxyParam) {
        OrderStatus orderStatus = (OrderStatus)RedisClient.getObj(proxyParam.getOrderId(), OrderStatus.class);
        if(orderStatus == null) {
            LOGGER.info("Redis没有[{}]的订单信息：{}",proxyParam.getOrderId());
            return false;
        }
        LOGGER.info(orderStatus.getOrderInfo().toString());
        return orderStatus.checkOrderInfo(proxyParam);
    }

//    /**
//     * @author <a href="mailto:wangfaqing@zhexinit.com" >王法清</a>
//     * 增加连接数
//     * @param proxyParam
//     */
//    public synchronized void connectOrderInfo(PublicProxyParam proxyParam) {
//        OrderStatus orderStatus = (OrderStatus)RedisClient.getObj(proxyParam.getOrderId(), OrderStatus.class);
//        if(orderStatus == null) {
//            LOGGER.info("Redis没有[{}]的订单信息：{}",proxyParam.getOrderId());
//            return;
//        }
//        String conKey = proxyParam.getOrderId() + "_" + proxyParam.getSecret();
//        RedisClient.incre(conKey, 1L);
//        //orderStatus.inrConn();
//        LOGGER.info("[{}]该订单增加连接数，目前已有连接数[{}]", proxyParam.getOrderId(), RedisClient.get(conKey));
//    }

    /**
     * @author <a href="mailto:wangfaqing@zhexinit.com" >王法清</a>1542556800
     * 更新流量
     * @param proxyParam
     * @param flow
     */
    public void updateOrderInfoFlow(PublicProxyParam proxyParam, Long flow) {
        String orderId = proxyParam.getOrderId();
        OrderStatus orderStatus = (OrderStatus)RedisClient.getObj(orderId, OrderStatus.class);
        if(orderStatus == null) {
            OrderInfo orderInfo = AppInstance.getOrderInfoMapper().getOrderById(orderId);
            if(orderInfo == null) {
                LOGGER.info("数据库没有[{}]的订单信息",proxyParam.getOrderId());
                return;
            }
            orderStatus = new OrderStatus(orderInfo);
            RedisClient.setObj(proxyParam.getOrderId(), orderStatus, 24 * 60 * 60);
        }
        orderStatus.updateFlow(flow);
    }

    /**
     * @author <a href="mailto:wangfaqing@zhexinit.com" >王法清</a>
     * 减少链接数
     * @param proxyParam
     */
    public synchronized void disconnectOrderInfo(PublicProxyParam proxyParam) {
        if(proxyParam == null) {
            return;
        }
        if (StringUtils.isEmpty(proxyParam.getErrorMsg())) {
            OrderStatus orderStatus = (OrderStatus)RedisClient.getObj(proxyParam.getOrderId(), OrderStatus.class);
            if(orderStatus == null) {
                LOGGER.info("Redis没有[{}]的订单信息：{}",proxyParam.getOrderId());
                return;
            }
            String secret = proxyParam.getSecret();
            if(StringUtils.isEmpty(secret)) {
                return;
            }
            String conKey = proxyParam.getOrderId() + "_" + secret;
            LOGGER.info("[{}]该订单断开一个连接, 当前剩余连接数[{}]", proxyParam.getOrderId(), RedisClient.incre(conKey, -1L));
        }
    }
}
