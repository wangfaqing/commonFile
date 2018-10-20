package org.vps.app.config;


public class ConfigStatic {

    private int servicePort;
	private String vpsProxyHost;       //代理服务器ip
	private int vpsProxyPort;          //代理服务器port

	public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }
    
	public String getVpsProxyHost() {
		return vpsProxyHost;
	}
	
	public void setVpsProxyHost(String vpsProxyHost) {
		this.vpsProxyHost = vpsProxyHost;
	}

    public int getVpsProxyPort() {
        return vpsProxyPort;
    }

    public void setVpsProxyPort(int vpsProxyPort) {
        this.vpsProxyPort = vpsProxyPort;
    }
	
}
