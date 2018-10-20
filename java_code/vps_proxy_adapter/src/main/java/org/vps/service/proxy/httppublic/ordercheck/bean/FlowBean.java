package org.vps.service.proxy.httppublic.ordercheck.bean;

public class FlowBean {
    /* 订单id */
    private String orderId;
    /* 使用的流量 */
    private Long flow;
    /* 流量最后更新时间 */
    private Long lastUpdate;

    public FlowBean(String orderId, Long flow, Long lastUpdate) {
        this.orderId = orderId;
        this.flow = flow;
        this.lastUpdate = lastUpdate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getFlow() {
        return flow;
    }

    public void setFlow(Long flow) {
        this.flow = flow;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
