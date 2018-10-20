package org.vps.service.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyStaticVar {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProxyStaticVar.class);
    
    public static final int PASS_SOCKS5_UDP_AUZ = 6;
    public static final int PASS_AUZ_UNABLE = 5;
    public static final int PASS_SOCKS5_AUZ = 4;
    public static final int PASS_HTTP_AUZ   = 3;
    public static final int PASS_INTRANET   = 2;
    public static final int PASS_WHITELIST  = 1;
    public static final int UNPASS          = 0;
    
    public static final int USER_TYPE_WHITELIST = 0;
    public static final int USER_TYPE_BLACKLIST = 1;
    public static final int USER_TYPE_OTHER     = 2;
}
