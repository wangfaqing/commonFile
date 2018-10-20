package org.vps.service.config;

public class ConfigStatic {
    private int vpsProxyPort;       //代理端口
    private String vpsLocalIP;      //代理内网ip
    private int vpsProxySocks5Port;

    public int getVpsProxyPort() {
        return vpsProxyPort;
    }

    public void setVpsProxyPort(int vpsProxyPort) {
        this.vpsProxyPort = vpsProxyPort;
    }
    
    public String getVpsLocalIP() {
        return vpsLocalIP;
    }

    public void setVpsLocalIP(String vpsLocalIP) {
        this.vpsLocalIP = vpsLocalIP;
    }

    public int getVpsProxySocks5Port() {
        return vpsProxySocks5Port;
    }

    public void setVpsProxySocks5Port(int vpsProxySocks5Port) {
        this.vpsProxySocks5Port = vpsProxySocks5Port;
    }

}
