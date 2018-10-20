package org.vps.app.log.dao.modle;

public class UserInfo {
    /* 用户id */
    private String id;
    /* 用户手机号 */
    private String mobile;
    /* 用户密钥 */
    private String secret;
    /* 账号状态 */
    private int accoutStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getAccoutStatus() {
        return accoutStatus;
    }

    public void setAccoutStatus(int accoutStatus) {
        this.accoutStatus = accoutStatus;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", secret='" + secret + '\'' +
                ", accoutStatus=" + accoutStatus +
                '}';
    }
}
