package org.vps.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringTools {
    private static final Logger logger = LoggerFactory.getLogger(StringTools.class);
    
    public static int String2Int(String str, int def) {
        int ret = def;
        try {
            if (str != null && !"".equals(str)) {
                ret = Integer.parseInt(str);
            } else {
                if (logger.isWarnEnabled()) {
//                    logger.warn("parseInt param warn, str: " + str);
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("parseInt error, str: " + str, e);
            }
        }
        return ret;
    }
    
    public static int String2Int(String str) {
        return String2Int(str, 0);
    }
    
}
