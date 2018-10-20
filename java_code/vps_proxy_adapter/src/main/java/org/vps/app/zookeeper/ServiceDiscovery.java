package org.vps.app.zookeeper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.zookeeper.ChildListener;
import com.alibaba.dubbo.remoting.zookeeper.StateListener;
import com.alibaba.dubbo.remoting.zookeeper.ZookeeperClient;
import com.alibaba.dubbo.remoting.zookeeper.zkclient.ZkclientZookeeperTransporter;

/**
 * 服务发现
 * @author liyimpc
 *
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);
    
    private URL zookeeperUrl;	// zookeeper注册地址

    private ZookeeperClient zkClient;	// zookeeper客户端
    
    private NodeCallBack nodeCallBack;	// 节点变更回调函数

    public ServiceDiscovery(URL zookeeperUrl) {
    	this.zookeeperUrl = zookeeperUrl;
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
     * 获取指定节点的服务地址信息
     * @return
     */
    public List<String> getServerList(String path) {
    	if (StringUtils.isBlank(path)) {
    		path = Constant.ROOT;
    	}
    	List<String> list = zkClient.getChildren(Constant.PATH_SEPARATOR + path);
    	// TODO 默认获取了所有的正常服务地址，可以考虑在此处添加负载均衡逻辑，通过持久化存储记录服务器调用的次数及上限，进行负债均衡控制。
    	return getSingerAddress(list);
    }

    /**
     * 客户端订阅服务，开启监听
     */
    public void doSubscribe() {
        	
    	// 添加状态监听，一般用于session过期做一些处理
        zkClient.addStateListener(new StateListener() {
            public void stateChanged(int state) {
            	if (state == RECONNECTED) {
	            	try {
	            		//TODO 记录失败的服务发布及服务订阅
	            		
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);	
					}
            	}
            }
        });

        // 添加节点监听，节点添加，变更，删除都会触发此监听
        zkClient.addChildListener(Constant.PATH_SEPARATOR + Constant.ROOT, new ChildListener() {
			@Override
			public void childChanged(String path, List<String> children) {
				nodeCallBack.changeCache(getSingerAddress(children));
			}
        });
        
    }
    
    /**
     * 客户端取消订阅服务
     */
    public void doUnsubscribe() {
    	//TODO
    }
    
	public void setNodeCallBack(NodeCallBack nodeCallBack) {
		this.nodeCallBack = nodeCallBack;
	}
    
    /**
     * 
     * main:TODO 请描述该方法是做什么用途的
     * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
     * @param args
     */
    
    public void flush() {
        String serviceUrl = Constant.FLUSH + ":" + new Date().getTime();
        zkClient.create(Constant.PATH_SEPARATOR + Constant.ROOT + Constant.PATH_SEPARATOR + serviceUrl, true);
        zkClient.delete(Constant.PATH_SEPARATOR + Constant.ROOT + Constant.PATH_SEPARATOR + serviceUrl);
    }
    
	/**
	 * 服务地址去重
	 * @param mgrList
	 * @return
	 */
	public List<String> getSingerAddress(List<String> mgrList) {
		List<String> list = new ArrayList<String>();
		if (null != mgrList && !mgrList.isEmpty()) {
			for (String path : mgrList) {
				path = path.substring(0, path.lastIndexOf(":"));
				if (!list.contains(path)) {
					list.add(path);
				}
			}
		}
		return list;
	}
  
}
