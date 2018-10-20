package org.vps.app.log.dao.modle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.util.DateTools;
import org.vps.app.util.StringTools;

public class LogProxySession implements Cloneable {
    
    private static final Logger logger = LoggerFactory.getLogger(LogProxySession.class);

    private String ipServer;            // 服务器ip（内网）
    private String ipRequest;           // 代理请求调用者ip
    private String url;                 // 请求的目标地址
    private String ipConnect;           // 代理使用的ip与端口号
    private String ipHeader;            // 代理头中指定的ip
    private String ipMD5Header;         // 代理User-Agent中指定的ip的MD5值
    private long connectTimestamp;      // 请求时间
    private long disconnectTimestamp;   // 请求断开时间
    private int recvByteCounts;         // 代理请求收到的字节数
    private int sendByteCounts;         // 代理请求响应的字节数
    private int requestTime;            // 一次connect后发送的http请求数
    private int sessionID;              // 一次connect后分配的唯一ID
    
    private int userType;               // 用户类型 白名单 :0 黑名单 :1 other:2
    private int pass;                   // 是否可以进行访问 不可访问:0 白名单可访问:1 鉴权可访问:2
    
    private int hasAUZ;                 // 鉴权是否存在 不存在:0 存在:1
    private String AUZInfo;             // http鉴权原始串  AUZ
    private String user;                // http鉴权用户名
    private String did;                 // http鉴权设备id
    private String uid;                 // http鉴权ip稳定Key
    private String uuid;                // http鉴权uuid
    private int pid;                    // http鉴权pid
    private int cid;                    // http鉴权cid

    private String mark;                // 传入参数mark
    private int sip = -1;               // 传入参数sip
    private int dip = -1;               // 传入参数dip
    private String ip;                  // 传入参数ip
    private int ipl = 0;                // 传入参数ipl
    private int ipmuc = 0;              // 传入参数ipmuc
    private String base;                // 传入参数base
    
    private int pid_u = -1;             // 实际使用的ip所在的省份
    private int cid_u = -1;             // 实际使用的ip所在的城市
    
    private int currentDate;            // 用于数据库表日期
    private String createTime;          // 日志创建时间
	
	public LogProxySession (String ipServer, String ipRequest) {
	    this.ipServer = ipServer;
	    this.ipRequest = ipRequest;
	    url = ipConnect = ipHeader = ipMD5Header = "";
	    connectTimestamp = disconnectTimestamp = recvByteCounts = sendByteCounts = requestTime = sessionID = 0;
	    userType = 3;
	    pass = 0;
	    hasAUZ = 0;
	    AUZInfo = user = did = uid = uuid = "";
	    pid = cid = -1;
	    createTime = DateTools.getTime();
        setCurrentDate(StringTools.String2Int(DateTools.getDate()));
	}

    /**
     * @return the ipServer
     */
    public String getIpServer() {
        return ipServer;
    }

    /**
     * @param ipServer the ipServer to set
     */
    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }

    /**
     * @return the ipRequest
     */
    public String getIpRequest() {
        return ipRequest;
    }

    /**
     * @param ipRequest the ipRequest to set
     */
    public void setIpRequest(String ipRequest) {
        this.ipRequest = ipRequest;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the ipConnect
     */
    public String getIpConnect() {
        return ipConnect;
    }

    /**
     * @param ipConnect the ipConnect to set
     */
    public void setIpConnect(String ipConnect) {
        this.ipConnect = ipConnect;
    }

    /**
     * @return the ipHeader
     */
    public String getIpHeader() {
        return ipHeader;
    }

    /**
     * @param ipHeader the ipHeader to set
     */
    public void setIpHeader(String ipHeader) {
        this.ipHeader = ipHeader;
    }

    /**
     * @return the ipMD5Header
     */
    public String getIpMD5Header() {
        return ipMD5Header;
    }

    /**
     * @param ipMD5Header the ipMD5Header to set
     */
    public void setIpMD5Header(String ipMD5Header) {
        this.ipMD5Header = ipMD5Header;
    }

    /**
     * @return the connectTimestamp
     */
    public long getConnectTimestamp() {
        return connectTimestamp;
    }

    /**
     * @param connectTimestamp the connectTimestamp to set
     */
    public void setConnectTimestamp(long connectTimestamp) {
        this.connectTimestamp = connectTimestamp;
    }

    /**
     * @return the disconnectTimestamp
     */
    public long getDisconnectTimestamp() {
        return disconnectTimestamp;
    }

    /**
     * @param disconnectTimestamp the disconnectTimestamp to set
     */
    public void setDisconnectTimestamp(long disconnectTimestamp) {
        this.disconnectTimestamp = disconnectTimestamp;
    }

    /**
     * @return the recvByteCounts
     */
    public int getRecvByteCounts() {
        return recvByteCounts;
    }

    /**
     * @param recvByteCounts the recvByteCounts to set
     */
    public void setRecvByteCounts(int recvByteCounts) {
        this.recvByteCounts = recvByteCounts;
    }

    /**
     * @return the sendByteCounts
     */
    public int getSendByteCounts() {
        return sendByteCounts;
    }

    /**
     * @param sendByteCounts the sendByteCounts to set
     */
    public void setSendByteCounts(int sendByteCounts) {
        this.sendByteCounts = sendByteCounts;
    }

    /**
     * @return the requestTime
     */
    public int getRequestTime() {
        return requestTime;
    }

    /**
     * @param requestTime the requestTime to set
     */
    public void setRequestTime(int requestTime) {
        this.requestTime = requestTime;
    }

    /**
     * @return the sessionID
     */
    public int getSessionID() {
        return sessionID;
    }

    /**
     * @param sessionID the sessionID to set
     */
    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * @return the userType
     */
    public int getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(int userType) {
        this.userType = userType;
    }

    /**
     * @return the pass
     */
    public int getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(int pass) {
        this.pass = pass;
    }

    /**
     * @return the hasAUZ
     */
    public int getHasAUZ() {
        return hasAUZ;
    }

    /**
     * @param hasAUZ the hasAUZ to set
     */
    public void setHasAUZ(int hasAUZ) {
        this.hasAUZ = hasAUZ;
    }

    /**
     * @return the aUZInfo
     */
    public String getAUZInfo() {
        return AUZInfo;
    }

    /**
     * @param aUZInfo the aUZInfo to set
     */
    public void setAUZInfo(String aUZInfo) {
        AUZInfo = aUZInfo;
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

    /**
     * @return the cid
     */
    public int getCid() {
        return cid;
    }

    /**
     * @param cid the cid to set
     */
    public void setCid(int cid) {
        this.cid = cid;
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
     * @return the pid_u
     */
    public int getPid_u() {
        return pid_u;
    }

    /**
     * @param pid_u the pid_u to set
     */
    public void setPid_u(int pid_u) {
        this.pid_u = pid_u;
    }

    /**
     * @return the cid_u
     */
    public int getCid_u() {
        return cid_u;
    }

    /**
     * @param cid_u the cid_u to set
     */
    public void setCid_u(int cid_u) {
        this.cid_u = cid_u;
    }

    /**
     * @return the currentDate
     */
    public int getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(int currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public LogProxySession clone() {
        try {
            return (LogProxySession)super.clone();    
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }
    
}
