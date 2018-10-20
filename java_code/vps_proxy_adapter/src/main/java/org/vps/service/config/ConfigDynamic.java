package org.vps.service.config;


import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.util.StringTools;
import org.vps.app.util.keyValue;

public class ConfigDynamic implements Cloneable {
    @DBAnnotation(name="", autoSet=false)
    private static final Logger logger = LoggerFactory.getLogger(ConfigDynamic.class);

    @DBAnnotation(name="proxy.http.host.list")
    private String strProxyHttpHostList;

    @DBAnnotation(name="proxy.socks5.host.list")
    private String strProxySocks5HostList;
    
    @DBAnnotation(name="", autoSet=false)
    private ArrayList<keyValue<String, Integer>> listProxyHttpHost;
    
    @DBAnnotation(name="", autoSet=false)
    private ArrayList<keyValue<String, Integer>> listProxySocks5Host;

    public String getStrProxySocks5HostList() {
		return strProxySocks5HostList;
	}

	public void setStrProxySocks5HostList(String strProxySocks5HostList) {
		this.strProxySocks5HostList = strProxySocks5HostList;
	}

	public String getStrProxyHttpHostList() {
        return strProxyHttpHostList;
    }

    public void setStrProxyHttpHostList(String strProxyHttpHostList) {
        this.strProxyHttpHostList = strProxyHttpHostList;
    }
    
    public ArrayList<keyValue<String, Integer>> getListProxyHttpHost() {
        if (listProxyHttpHost != null) return listProxyHttpHost;
        synchronized (this) {
            if (listProxyHttpHost != null) return listProxyHttpHost;
            if (strProxyHttpHostList != null) {
                ArrayList<keyValue<String, Integer>> list = new ArrayList<>();
                String []splite = strProxyHttpHostList.split(";");
                for(String hostPortList : splite) {
                    String []hostPort = hostPortList.split(":");
                    if (hostPort.length == 2) {
                        String host = hostPort[0];
                        Integer port = new Integer(StringTools.String2Int(hostPort[1], 0));
                        if (port != 0) {
                            list.add(new keyValue<String, Integer>(host, port));
                        }
                    }
                }
                if (list.size() > 0) {
                    listProxyHttpHost = list;
                }
            }
        }
        return listProxyHttpHost;
    }
    
    public ArrayList<keyValue<String, Integer>> getListProxySocks5Host() {
        if (listProxySocks5Host != null) return listProxySocks5Host;
        synchronized (this) {
            if (listProxySocks5Host != null) return listProxySocks5Host;
            if (strProxySocks5HostList != null) {
                ArrayList<keyValue<String, Integer>> list = new ArrayList<>();
                String []splite = strProxySocks5HostList.split(";");
                for(String hostPortList : splite) {
                    String []hostPort = hostPortList.split(":");
                    if (hostPort.length == 2) {
                        String host = hostPort[0];
                        Integer port = new Integer(StringTools.String2Int(hostPort[1], 0));
                        if (port != 0) {
                            list.add(new keyValue<String, Integer>(host, port));
                        }
                    }
                }
                if (list.size() > 0) {
                    listProxySocks5Host = list;
                }
            }
        }
        return listProxySocks5Host;
    }
    
    @Override
    public Object clone() {
        ConfigDynamic configDynamic = null;
        try {
            configDynamic = (ConfigDynamic)super.clone();
            configDynamic.listProxyHttpHost = null;
            configDynamic.listProxySocks5Host = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return configDynamic;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((strProxyHttpHostList == null) ? 0 : strProxyHttpHostList.hashCode());
        result = prime * result + ((strProxySocks5HostList == null) ? 0 : strProxySocks5HostList.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigDynamic other = (ConfigDynamic) obj;
        if (strProxyHttpHostList == null) {
            if (other.strProxyHttpHostList != null)
                return false;
        } else if (!strProxyHttpHostList.equals(other.strProxyHttpHostList))
            return false;
        if (strProxySocks5HostList == null) {
            if (other.strProxySocks5HostList != null)
                return false;
        } else if (!strProxySocks5HostList.equals(other.strProxySocks5HostList))
            return false;
        return true;
    }

}