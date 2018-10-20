package org.vps.service.proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationInfo implements Cloneable {
    public static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationInfo.class);
    
    private String username;
    private String did;
    private String uid;
    private String uuid;
    private String ip;
    private String ipMD5;
    private int sid = -2;
    private int pid;
    private int cid;
    private int dip;
    private int sip;
    private int mod;
    private int ipl;        // ip限制开关， 开：1，关：0
    private int ipmuc;      // ip限制上限
    private String base;    // ip限制，基地所属
    private String mark;    
    private long t;
    private String sign;
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @return the did
     */
    public String getDid() {
        return did;
    }
    
    /**
     * @param did the did to set
     */
    public void setDid(String did) {
        this.did = did;
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
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }
    
    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the ipMD5
     */
    public String getIpMD5() {
        return ipMD5;
    }

    /**
     * @param ipMD5 the ipMD5 to set
     */
    public void setIpMD5(String ipMD5) {
        this.ipMD5 = ipMD5;
    }

    /**
     * @return the sid
     */
    public int getSid() {
        return sid;
    }

    /**
     * @param sid the sid to set
     */
    public void setSid(int sid) {
        this.sid = sid;
    }

    /**
     * @return the pid
     */
    public int getPid() {
        return pid;
    }
    
    public int getArea() {
        return  (sid == -2) ? getPid() : getSid();
    }
    
    /**
     * @param pid the pid to set
     */
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    /**
     * @return the cid
     */
    public int getCid() {
        return cid;
    }
    
    public int getSubarea() {
        return getCid();
    } 
    
    /**
     * @param cid the cid to set
     */
    public void setCid(int cid) {
        this.cid = cid;
    }
    
    /**
     * @return the dip
     */
    public int getDip() {
        return dip;
    }

    /**
     * @param dip the dip to set
     */
    public void setDip(int dip) {
        this.dip = dip;
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
     * @return the mod
     */
    public int getMod() {
        return mod;
    }

    /**
     * @param mod the mod to set
     */
    public void setMod(int mod) {
        this.mod = mod;
    }

    /**
     * @return the ipl
     */
    public int getIpl() {
        return ipl;
    }

    /**
     * @param ipl the ipl to set
     */
    public void setIpl(int ipl) {
        this.ipl = ipl;
    }

    /**
     * @return the ipmuc
     */
    public int getIpmuc() {
        return ipmuc;
    }

    /**
     * @param ipmuc the ipmuc to set
     */
    public void setIpmuc(int ipmuc) {
        this.ipmuc = ipmuc;
    }

    /**
     * @return the base
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(String base) {
        this.base = base;
    }

    /**
     * @return the mark
     */
    public String getMark() {
        return mark;
    }

    /**
     * @param mark the mark to set
     */
    public void setMark(String mark) {
        this.mark = mark;
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
 
    @Override
    public Object clone() {
        AuthorizationInfo info = null;
        try {
            info = (AuthorizationInfo)super.clone();
        } catch (Exception e) {
 
        }
        return info;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AuthorizationInfo [username=" + username + ", did=" + did + ", uid=" + uid + ", uuid=" + uuid + ", ip="
                + ip + ", ipMD5=" + ipMD5 + ", sid=" + sid + ", pid=" + pid + ", cid=" + cid + ", dip=" + dip + ", sip="
                + sip + ", mod=" + mod + ", ipl=" + ipl + ", ipmuc=" + ipmuc + ", base=" + base + ", t=" + t + ", sign="
                + sign + "]";
    }
    
}
