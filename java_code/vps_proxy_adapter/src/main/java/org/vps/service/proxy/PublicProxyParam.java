package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicProxyParam implements Cloneable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProxyParam.class);
    
    private String user;    // 用户
    private int rid;        // 并发id
    private long t;         // 时间
    private String sign;    // 签名
    private String uid;     // 用户id
    private int sip;        // 单ip
    private int pid;        // 省份

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the rid
     */
    public int getRid() {
        return rid;
    }

    /**
     * @param rid the rid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     * @return the t
     */
    public long getT() {
        return t;
    }

    /**
     * @param t the t to set
     */
    public void setT(long t) {
        this.t = t;
    }

    /**
     * @return the sign
     */
    public String getSign() {
        return sign;
    }

    /**
     * @param sign the sign to set
     */
    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the sip
     */
    public int getSip() {
        return sip;
    }

    /**
     * @param sip the sip to set
     */
    public void setSip(int sip) {
        this.sip = sip;
    }

    /**
     * @return the pid
     */
    public int getPid() {
        return pid;
    }

    /**
     * @param pid the pid to set
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public Object clone() {
        PublicProxyParam info = null;
        try {
            info = (PublicProxyParam)super.clone();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return info;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ProxyParamPublic [user=" + user + ", rid=" + rid + ", t=" + t + ", sign=" + sign + ", uid=" + uid
                + ", sip=" + sip + ", pid=" + pid + "]";
    }

}
