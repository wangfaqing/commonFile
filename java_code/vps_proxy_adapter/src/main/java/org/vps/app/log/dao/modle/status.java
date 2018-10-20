package org.vps.app.log.dao.modle;

public enum status {
    ENABLE, DISABLE;
    
    public static status valueOf(boolean value) {
        if (value) 
            return status.ENABLE;
        return DISABLE;
    } 
}
