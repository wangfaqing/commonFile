package org.vps.app;


import java.util.Calendar;
import java.util.Date;

import javax.xml.crypto.Data;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.vps.app.cms.AppInstance;
import org.vps.app.redis.ProxyOrderBase;
import org.vps.app.redis.ProxyTransferTb;
import org.vps.app.redis.RedisClient;
import org.vps.app.user.PublicUsers;
import org.vps.service.listen.AppEntry;
import org.vps.service.listen.ServiceEntry;
import org.vps.service.mgr.VpsCMS;
import org.vps.service.workdirect.DirectBootstrap;
import org.vps.service.workdirect.Socks5DirectBootstrap;

import redis.clients.jedis.JedisPool;

public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

	public static final Logger logger = LoggerFactory.getLogger(InstantiationTracingBeanPostProcessor.class);

	/**
	 * @author sund
	 * @des 当spring 容器初始化完成后执行此方法进行ip表数据初始化以及相关初始服务
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (event.getApplicationContext().getParent() == null) {
		    //保留一份spring上下文
		    AppInstance.setApplicationContext(event.getApplicationContext());
			
		    
		    AppInstance.setUsers(new PublicUsers(AppInstance.getUserMapper().getPublicUser()));
		    
		    ProxyTransferTb.setJedisPool((JedisPool)AppInstance.getApplicationContext().getBean("jedisPool"));
		    ProxyOrderBase.setJedisPool((JedisPool)AppInstance.getApplicationContext().getBean("jedisPool"));
			RedisClient.setJedisPool((JedisPool)AppInstance.getApplicationContext().getBean("jedisPool"));
//		    ProxyOrderBase.listPushRight("POR-" + "ly", "ly-000001"+"_" + 1048576 + "_" + 1000);
//		    int seconds = (int) (86400 - DateUtils.getFragmentInSeconds(Calendar.getInstance(), Calendar.DATE));
//		    ProxyOrderBase.set("POR-" + "ly-000001" + "_" + "flow", "1048576", seconds);
//		    ProxyOrderBase.set("POR-" + "ly-000001" + "_" + "req", "1000", seconds);
		    
			//app config 附加初始化
//			Config config = (Config)event.getApplicationContext().getBean("appConfig");
//			ConfigDynamic configDynamic = (ConfigDynamic)event.getApplicationContext().getBean("appConfigDynamic");
//			config.setConfigDynamic(configDynamic);

	        //service config 附加初始化
			org.vps.service.config.Config serviceConfig = (org.vps.service.config.Config)event.getApplicationContext().getBean("serviceConfig");
			org.vps.service.config.ConfigDynamic serviceConfigDynamic = (org.vps.service.config.ConfigDynamic)event.getApplicationContext().getBean("serviceConfigDynamic");
			serviceConfig.setConfigDynamic(serviceConfigDynamic);
			
            //启动直连代理服务
            DirectBootstrap directBootstrap = new DirectBootstrap();
            directBootstrap.init();
            VpsCMS.setDirectBoootstrap(directBootstrap);
            
            Socks5DirectBootstrap socks5DirectBootstrap = new Socks5DirectBootstrap();
            socks5DirectBootstrap.init();
            VpsCMS.setSocks5DirectBootstrap(socks5DirectBootstrap);
            
			//vps service服务器启动（http代理服务，socks5代理服务, 监控服务）
			try {
			    AppEntry.business(AppInstance.getConfig().getServicePort());
			    ServiceEntry.httpProxy("0.0.0.0", serviceConfig.getVpsProxyPort());
			    ServiceEntry.httpProxy2("0.0.0.0", 14222);
		        ServiceEntry.socks5Proxy("0.0.0.0", serviceConfig.getVpsProxySocks5Port());
			} catch (Exception e) {
			    logger.error(e.getMessage(), e);
			}
		}
	}

}
