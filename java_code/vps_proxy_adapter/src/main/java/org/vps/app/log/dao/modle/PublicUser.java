package org.vps.app.log.dao.modle;

public class PublicUser {
    
    private int id;         // 表行id
    private String user;    // 用户名
    private String key;     // 用户密钥
    private int uidLimit;   // 
    private int conLimit;   // 
    private boolean used;   //
    private String desc;    // 描述
    
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
     * @return the key
     */
    public String getKey() {
        return key;
    }
    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }
    /**
     * @return the uidLimit
     */
    public int getUidLimit() {
        return uidLimit;
    }
    /**
     * @param uidLimit the uidLimit to set
     */
    public void setUidLimit(int uidLimit) {
        this.uidLimit = uidLimit;
    }
    /**
     * @return the conLimit
     */
    public int getConLimit() {
        return conLimit;
    }
    /**
     * @param conLimit the conLimit to set
     */
    public void setConLimit(int conLimit) {
        this.conLimit = conLimit;
    }
    /**
     * @return the used
     */
    public boolean getUsed() {
        return used;
    }
    /**
     * @param used the used to set
     */
    public void setUsed(boolean used) {
        this.used = used;
    }
    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }
    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PublicUser [id=" + id + ", user=" + user + ", key=" + key + ", uidLimit=" + uidLimit + ", conLimit="
                + conLimit + ", used=" + used + ", desc=" + desc + "]";
    }
    
}
