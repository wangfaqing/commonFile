package org.vps.app.cms;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.vps.app.config.Config;
import org.vps.app.log.LogExecution;
import org.vps.app.log.dao.mapper.OrderInfoMapper;
import org.vps.app.log.dao.mapper.OrderUsedUpdateMapper;
import org.vps.app.log.dao.mapper.PublicUserMapper;
import org.vps.app.log.dao.mapper.UserInfoMapper;
import org.vps.app.log.dao.modle.OrderUsed;
import org.vps.app.user.PublicUsers;
import org.vps.service.proxy.PublicUserOrderMgr;
import org.vps.service.proxy.httppublic.ordercheck.PublicOrderInfoManage;

@Service("appInstance")
public class AppInstance {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AppInstance.class);
    
    private static ApplicationContext applicationContext;   //spring 的上下文
    
    private static AppInstance m; 
    @Autowired
    private Config config;   //app 的config
    @Autowired
    private LogExecution logExecution;
    @Autowired
    private PublicUserMapper userMapper;
    @Autowired
    private PublicUserOrderMgr publicUserOrderMgr;
    @Autowired 
    private OrderUsedUpdateMapper orderUsedUpdateMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PublicOrderInfoManage orderInfoManage;
    
    private PublicUsers publicUsers;
//    @Autowired
//    private CachedHolder ehcache;
    // 定时任务
//    @Autowired
//    private Timer timer = new HashedWheelTimer();
    
    public static void init() {
        m = (AppInstance)AppInstance.applicationContext.getBean("appInstance");
    }
    
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        AppInstance.applicationContext = applicationContext;
        init();
    }

    public static Config getConfig() {
        return m.config;
    }

    public static LogExecution getLogExecution() {
        return m.logExecution;
    }

    public static PublicUserMapper getUserMapper() {
        return m.userMapper;
    }

    public static PublicUsers getPublicUsers() {
        return m.publicUsers;
    }

//    public static Timer getTimer() {
//        return m.timer;
//    }

//    public static CachedHolder getEhcache() {
//        return m.ehcache;
//    }

    public static PublicUserOrderMgr getPublicUserOrderMgr() {
        return m.publicUserOrderMgr;
    }


    public static void setUsers(PublicUsers publicUsers) {
        m.publicUsers = publicUsers;
    }
    
    public static OrderUsedUpdateMapper getOrderUsedUpdateMapper() {
        return m.orderUsedUpdateMapper;
    }


    public static OrderInfoMapper getOrderInfoMapper(){
        return m.orderInfoMapper;
    }

    public static UserInfoMapper getUserInfoMapper() {
        return m.userInfoMapper;
    }

    public static PublicOrderInfoManage getOrderInfoManage() {
        return m.orderInfoManage;
    }
}
