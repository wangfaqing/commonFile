package org.vps.service.proxy;


import java.util.ArrayList;

import org.vps.app.log.dao.modle.LogProxySession;

public class AuthorizationCheckInfo {
    private boolean timeCheck;
    private boolean uuidCheck;
    private boolean authorizationEnable;
    private int pass;
    private AuthorizationInfo info;
    private ArrayList<String> ipWhiteList;
    private ArrayList<String> ipBlackList;
    private LogProxySession logProxySession;
    
    public AuthorizationCheckInfo(
            int pass, 
            AuthorizationInfo info, 
            boolean authorizationEnable, 
            ArrayList<String> ipWhiteList, 
            ArrayList<String> ipBlackList,
            LogProxySession logProxySession) {
        this.setAuthorizationEnable(authorizationEnable);
        this.pass = pass;
        this.info = info;
        this.ipWhiteList = ipWhiteList;
        this.ipBlackList = ipBlackList;
        this.logProxySession = logProxySession;
        uuidCheck = false;
        timeCheck = false;
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
    public AuthorizationInfo getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(AuthorizationInfo info) {
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