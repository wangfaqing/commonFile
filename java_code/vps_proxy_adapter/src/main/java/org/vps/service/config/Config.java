package org.vps.service.config;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vps.service.config.ConfigStatic;
import org.vps.app.util.keyValue;
import org.vps.service.config.ConfigDynamic;

@Service("serviceConfig")
public class Config {
    
    private AtomicReference<ConfigDynamic> arConfigDynamic = new AtomicReference<>();
    
    @Autowired
    private ConfigStatic configStatic;
    
    public void setConfigDynamic(ConfigDynamic configDynamic) {
        arConfigDynamic.set(configDynamic);
    }
    
    public ConfigDynamic getConfigDynamic() {
        return arConfigDynamic.get();
    } 

    public int getVpsProxyPort() {
        return configStatic.getVpsProxyPort();
    }
    
    public String getVpsLocalIP() {
        return configStatic.getVpsLocalIP();
    }
    
    public int getVpsProxySocks5Port() {
        return configStatic.getVpsProxySocks5Port();
    }
    
    public ArrayList<keyValue<String, Integer>> getListProxyHttpHost() {
        return arConfigDynamic.get().getListProxyHttpHost();
    }
    
    public ArrayList<keyValue<String, Integer>> getListProxySocks5Host() {
        return arConfigDynamic.get().getListProxySocks5Host();
    }
}
