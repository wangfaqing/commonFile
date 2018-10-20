package org.vps.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.service.config.DBAnnotation;

public class ConfigDynamic implements Cloneable {
    @DBAnnotation(name="", autoSet=false)
    private static final Logger logger = LoggerFactory.getLogger(ConfigDynamic.class);
    
    @DBAnnotation(name="log.write.file")
    private boolean logWriteFile;           //日志写文件
    
    @DBAnnotation(name="log.write.db")
    private boolean logWriteDB;             //日志写数据库
    
    public boolean isLogWriteFile() {
        return logWriteFile;
    }

    public void setLogWriteFile(boolean logWriteFile) {
        this.logWriteFile = logWriteFile;
    }

    public boolean isLogWriteDB() {
        return logWriteDB;
    }

    public void setLogWriteDB(boolean logWriteDB) {
        this.logWriteDB = logWriteDB;
    }
    
    @Override
    public Object clone() {
        ConfigDynamic configDynamic = null;
        try {
            configDynamic = (ConfigDynamic)super.clone();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
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
        result = prime * result + (logWriteDB ? 1231 : 1237);
        result = prime * result + (logWriteFile ? 1231 : 1237);
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
        if (logWriteDB != other.logWriteDB)
            return false;
        if (logWriteFile != other.logWriteFile)
            return false;
        return true;
    }

}