package org.vps.app.config;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("appConfig")
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
    
    public int getServicePort() {
        return configStatic.getServicePort();
    } 
    
    public String getVpsProxyHost() {
        return configStatic.getVpsProxyHost();
    }

    public int getVpsProxyPort() {
        return configStatic.getVpsProxyPort();
    }
    
    public boolean isLogWriteFile() {
        return arConfigDynamic.get().isLogWriteFile();
    }

    public boolean isLogWriteDB() {
        return arConfigDynamic.get().isLogWriteDB();
    }

}
