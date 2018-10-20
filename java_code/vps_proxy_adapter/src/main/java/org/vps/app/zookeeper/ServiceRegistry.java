package org.vps.app.zookeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import com.alibaba.dubbo.remoting.zookeeper.zkclient.ZkclientZookeeperTransporter;

/**
 * 服务注册
 * @author liyimpc
 *
 */
public class ServiceRegistry {

    private final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private URL zookeeperUrl;	// zookeeper注册地址

    private String serviceUrl;	// 服务注册地址
    
    private ZookeeperClient zkClient;	// zookeeper客户端

    public ServiceRegistry(URL zookeeperUrl, String serviceUrl) {
    	this.zookeeperUrl = zookeeperUrl;
        this.serviceUrl = serviceUrl;
        zkClient = connectServer();
    }

    /**
     * 初始化连接Zookeeper
     * @return
     */
    private ZookeeperClient connectServer() {
  	    ZkclientZookeeperTransporter transporter = new ZkclientZookeeperTransporter();
	    zkClient = transporter.connect(zookeeperUrl);
        return zkClient;
    }
    
    /**
     * 注册服务方法
     */
    public void doRegister() {
        try {
        	// 注册根节点
        	zkClient.create(Constant.PATH_SEPARATOR + Constant.ROOT, false);
        	// 注册子节点
        	zkClient.create(Constant.PATH_SEPARATOR + Constant.ROOT + Constant.PATH_SEPARATOR + serviceUrl, true);
        } catch (Throwable e) {
        	LOGGER.debug("Failed to register " + serviceUrl + " to zookeeper, cause: " + e.getMessage(), e);
        }
    }

    /**
     * 销毁服务注册
     */
    public void doUnregister() {
        try {
        	//TODO 先循环删除子节点，再删除根节点
            zkClient.delete(Constant.PATH_SEPARATOR + serviceUrl);
        } catch (Throwable e) {
        	LOGGER.debug("Failed to unregister " + serviceUrl + " to zookeeper, cause: " + e.getMessage(), e);
        }
    }
}