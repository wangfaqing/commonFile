package org.vps.app.log.dao.modle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OrderInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /* 订单ID */
    private String orderId;
    /* 用户ID */
    private Long userId;
    /* 产品ID */
    private int productId;
    /* 并发数 */
    private int concurrentUsers;
    /* 并发数 */
    private int flowNumber;
    /* 订单金额 */
    private BigDecimal orderPrice;
    /* 订单状态 */
    private int orderStatus;
    /* 订单有效日期 单位：日 */
    private Long orderExpiryDate;
    /* 订单失败信息 */
    private String orderFailMessage;
    /* 订单创建时间 */
    private Date orderCreateTime;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getOrderExpiryDate() {
        return orderExpiryDate;
    }

    public void setOrderExpiryDate(Long orderExpiryDate) {
        this.orderExpiryDate = orderExpiryDate;
    }

    public String getOrderFailMessage() {
        return orderFailMessage;
    }

    public void setOrderFailMessage(String orderFailMessage) {
        this.orderFailMessage = orderFailMessage;
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productId=" + productId +
                ", concurrentUsers=" + concurrentUsers +
                ", flowNumber=" + flowNumber +
                ", orderPrice=" + orderPrice +
                ", orderStatus=" + orderStatus +
                ", orderExpiryDate=" + orderExpiryDate +
                ", orderFailMessage='" + orderFailMessage + '\'' +
                ", orderCreateTime=" + orderCreateTime +
                '}';
    }
}
