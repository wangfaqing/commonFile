package org.vps.service.proxy.httppublic.ordercheck;


import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.service.proxy.httppublic.ordercheck.bean.PublicProxyParam;


import java.util.ArrayList;

public class PublicAuthorizationCheckInfo {
    private boolean timeCheck;
    private boolean uuidCheck;
    private boolean authorizationEnable;
    private int pass;
    private PublicProxyParam info;
    private ArrayList<String> ipWhiteList;
    private ArrayList<String> ipBlackList;
    private LogProxySession logProxySession;
    
    public PublicAuthorizationCheckInfo(
            int pass, 
            PublicProxyParam info, 
            boolean authorizationEnable, 
            ArrayList<String> ipWhiteList, 
            ArrayList<String> ipBlackList,
            LogProxySession logProxySession) {
        this.authorizationEnable = authorizationEnable;
        this.pass = pass;
        this.info = info;
        this.ipWhiteList = ipWhiteList;
        this.ipBlackList = ipBlackList;
        this.logProxySession = logProxySession;
        uuidCheck = false;
        timeCheck = false;
    }

    /**
     * @return the timeCheck
     */
    public boolean isTimeCheck() {
        return timeCheck;
    }

    /**
     * @param timeCheck the timeCheck to set
     */
    public void setTimeCheck(boolean timeCheck) {
        this.timeCheck = timeCheck;
    }

    /**
     * @return the uuidCheck
     */
    public boolean isUuidCheck() {
        return uuidCheck;
    }

    /**
     * @param uuidCheck the uuidCheck to set
     */
    public void setUuidCheck(boolean uuidCheck) {
        this.uuidCheck = uuidCheck;
    }

    /**
     * @return the authorizationEnable
     */
    public boolean isAuthorizationEnable() {
        return authorizationEnable;
    }

    /**
     * @param authorizationEnable the authorizationEnable to set
     */
    public void setAuthorizationEnable(boolean authorizationEnable) {
        this.authorizationEnable = authorizationEnable;
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
     * @return the info
     */
    public PublicProxyParam getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(PublicProxyParam info) {
        this.info = info;
    }

    /**
     * @return the ipWhiteList
     */
    public ArrayList<String> getIpWhiteList() {
        return ipWhiteList;
    }

    /**
     * @param ipWhiteList the ipWhiteList to set
     */
    public void setIpWhiteList(ArrayList<String> ipWhiteList) {
        this.ipWhiteList = ipWhiteList;
    }

    /**
     * @return the ipBlackList
     */
    public ArrayList<String> getIpBlackList() {
        return ipBlackList;
    }

    /**
     * @param ipBlackList the ipBlackList to set
     */
    public void setIpBlackList(ArrayList<String> ipBlackList) {
        this.ipBlackList = ipBlackList;
    }

    /**
     * @return the logProxySession
     */
    public LogProxySession getLogProxySession() {
        return logProxySession;
    }

    /**
     * @param logProxySession the logProxySession to set
     */
    public void setLogProxySession(LogProxySession logProxySession) {
        this.logProxySession = logProxySession;
    }
    
}