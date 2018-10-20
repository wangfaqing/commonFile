package org.vps.service.proxy.httppublic.ordercheck.bean;

import java.util.Date;

public class PublicProxyParam implements Cloneable {

    private int pid;        // 省份

    // --- header 中包含的参数
    private String orderId;
    private String secret;
    private long time;
    private String sign;

    //--- orderInfo参数
    /* 用户ID */
    private String userId;
    /* 产品ID */
    private int productId;
    /* 并发数 */
    private int concurrentUsers;
    /* 流量 */
    private int flowNumber;

    /* 订单有效日 单位：日 */
    private int orderExpiryDate;

    /* 订单支付时间 */
    private Date payTime;

    /* 错误信息 */
    private String errorMsg;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getConcurrentUsers() {
        return concurrentUsers;
    }

    public void setConcurrentUsers(int concurrentUsers) {
        this.concurrentUsers = concurrentUsers;
    }

    public int getFlowNumber() {
        return flowNumber;
    }

    public void setFlowNumber(int flowNumber) {
        this.flowNumber = flowNumber;
    }

    public int getOrderExpiryDate() {
        return orderExpiryDate;
    }

    public void setOrderExpiryDate(int orderExpiryDate) {
        this.orderExpiryDate = orderExpiryDate;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    //    @Override
//    public Object clone() {
//        PublicProxyParam info = null;
//        try {
//            info = (PublicProxyParam)super.clone();
//        } catch (Exception e) {
//            LOGGER.warn(e.getMessage(), e);
//        }
//        return info;
//    }


}
