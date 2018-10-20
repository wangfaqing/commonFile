package org.vps.app.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.util.StringTools;

import edu.hziee.common.lang.StringUtil;

/**
 * 
 * 
 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
 * @version 1.0.0
 */
public class ProxyOrderRule {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxyOrderRule.class);
	
    private final static String PROFIX = "POR-";

	public static Order update(String orderId, long flow, long req) {
	    long newFlow = ProxyOrderBase.decr(PROFIX + orderId + "_flow", flow);
        long newReq = ProxyOrderBase.decr(PROFIX + orderId + "_req", req);
        Order order = new Order();
        order.setOrderId(orderId);
        order.setFlow(newFlow);
        order.setReq(newReq);
        return order;
	}
	
	public static Order getOrderUsed(String orderId) {
        long newFlow = ProxyOrderBase.decr(PROFIX + orderId + "_flow", 0);
        long newReq = ProxyOrderBase.decr(PROFIX + orderId + "_req", 0);
        Order order = new Order();
        order.setOrderId(orderId);
        order.setFlow(newFlow);
        order.setReq(newReq);
        return order;
    }
	
    public static Order getOrder(String user) {
        try {
            String orderDesc = ProxyOrderBase.listIndex(PROFIX + user, 0);
            if (!StringUtil.isBlank(orderDesc)) {
                String []split = orderDesc.split("_");
                Order order = new Order();
                order.setOrderId(split[0]);
                order.setFlow(StringTools.String2Int(split[1], 0));
                order.setReq(StringTools.String2Int(split[2], 0));
                return order;
            }    
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }
    
    public static boolean removeOrder(String user, String orderId) {
        String orderIdremove = ProxyOrderBase.listPopLeft(PROFIX + user);
        if (orderIdremove.indexOf(orderId) == 0) {
            return true;
        } else {
            ProxyOrderBase.listPushLeft(PROFIX + user, orderIdremove);
            return true;
        }        
    }

}
