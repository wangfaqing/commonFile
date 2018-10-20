package org.vps.service.proxy.httppublic.ordercheck.bean;

/**
 * @Author yangchonglun
 * @Date 15:54 2018/10/12
 * 从header中取出的内容
 **/
public class AuthorBean {
    private String orderId;
    private String secret;
    private long time;
    private String sign;


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
}
