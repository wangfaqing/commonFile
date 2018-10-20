package org.vps.service.mgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vps.service.config.Config;
import org.vps.service.workdirect.DirectBootstrap;
import org.vps.service.workdirect.Socks5DirectBootstrap;

@Service("vpscms")
public class VpsCMS {
    
	private static Config config;
	
    private static DirectBootstrap directBoootstrap;
    
    private static Socks5DirectBootstrap socks5DirectBootstrap;
    
    public static Config getConfig() {
        return config;
    }
    
    @Autowired
    public void setConfig(Config config) {
        VpsCMS.config = config;
    }

    public static DirectBootstrap getDirectBoootstrap() {
        return directBoootstrap;
    }

    public static void setDirectBoootstrap(DirectBootstrap directBoootstrap) {
        VpsCMS.directBoootstrap = directBoootstrap;
    }

	public static Socks5DirectBootstrap getSocks5DirectBootstrap() {
		return socks5DirectBootstrap;
	}

	public static void setSocks5DirectBootstrap(Socks5DirectBootstrap socks5DirectBootstrap) {
		VpsCMS.socks5DirectBootstrap = socks5DirectBootstrap;
	}

}
