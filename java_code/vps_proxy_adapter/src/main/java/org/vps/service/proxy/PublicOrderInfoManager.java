package org.vps.service.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vps.app.log.dao.modle.OrderInfo;

import java.util.HashMap;
import java.util.Map;

@Service("publicOrderInfoManager")
public class PublicOrderInfoManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(PublicOrderInfoManager.class);

    private Map<String, PublicOrderInfo> publicOrderInfoMap;

    public PublicOrderInfoManager() {
        publicOrderInfoMap = new HashMap<>();
    }

    /**
     * @author <a href="mailto:wangfaqing@zhexinit.com" >王法清</a>
     * 校验订单
     * @param orderInfo
     * @return
     */
    public boolean checkPublicOrderInfo(OrderInfo orderInfo) {
        PublicOrderInfo publicOrderInfo = publicOrderInfoMap.get(orderInfo.getOrderId());
        if(publicOrderInfo == null) {
            publicOrderInfo = new PublicOrderInfo(orderInfo);
        }
        return publicOrderInfo.checkOrderInfo();
    }

    public void connectPublicOrderInfo(OrderInfo orderInfo) {
        PublicOrderInfo publicOrderInfo = publicOrderInfoMap.get(orderInfo.getOrderId());
        if(publicOrderInfo == null) {
            publicOrderInfo = new PublicOrderInfo(orderInfo);
            publicOrderInfoMap.put(orderInfo.getOrderId(), publicOrderInfo);
        }
        publicOrderInfo.inrConn();
        LOGGER.info("[{}]该订单增加连接数，目前已有连接数[{}]", orderInfo.getOrderId(), publicOrderInfo.getConn());
    }

    /**
     * @author <a href="mailto:wangfaqing@zhexinit.com" >王法清</a>
     * 更新流量
     * @param orderInfo
     * @param flow
     */
    public void updatePublicOrderInfoFlow(OrderInfo orderInfo, Long flow) {
        PublicOrderInfo publicOrderInfo = publicOrderInfoMap.get(orderInfo.getOrderId());
        if(publicOrderInfo == null) {
            publicOrderInfo = new PublicOrderInfo(orderInfo);
        }
        publicOrderInfo.updateFlow(flow);
    }

    public void disconnectOrderInfo(OrderInfo orderInfo) {
        PublicOrderInfo publicOrderInfo = publicOrderInfoMap.get(orderInfo.getOrderId());
        if(publicOrderInfo == null) {
            return;
        }
        publicOrderInfo.decrConn();
    }

}
