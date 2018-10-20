package org.vps.app.redis;

public class Order {
    
    private String orderId;
    private long flow;
    private long req;
    
    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }
    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    /**
     * @return the flow
     */
    public long getFlow() {
        return flow;
    }
    /**
     * @param flow the flow to set
     */
    public void setFlow(long flow) {
        this.flow = flow;
    }
    /**
     * @return the req
     */
    public long getReq() {
        return req;
    }
    /**
     * @param req the req to set
     */
    public void setReq(long req) {
        this.req = req;
    }
    
}
