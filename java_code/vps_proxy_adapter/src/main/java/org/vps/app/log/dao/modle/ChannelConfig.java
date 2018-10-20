package org.vps.app.log.dao.modle;

public class ChannelConfig {
    private int id;             // 表行id
    private String appKey;      // 应用Key
    private String appChannel;  // 应用渠道
    private boolean loginReject;// 拒绝登入配置
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the appKey
     */
    public String getAppKey() {
        return appKey;
    }
    /**
     * @param appKey the appKey to set
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    /**
     * @return the appChannel
     */
    public String getAppChannel() {
        return appChannel;
    }
    /**
     * @param appChannel the appChannel to set
     */
    public void setAppChannel(String appChannel) {
        this.appChannel = appChannel;
    }
    
    public status isLoginReject() {
        return status.valueOf(loginReject);
    }
    
    public void setLoginReject(int loginReject) {
        if (loginReject != 0) {
            this.loginReject = true;
        } else {
            this.loginReject = false;
        }
    }
    
}
