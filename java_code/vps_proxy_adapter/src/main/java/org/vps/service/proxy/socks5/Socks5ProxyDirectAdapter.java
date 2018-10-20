package org.vps.service.proxy.socks5;


import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.redis.ProxyTransferRule;
import org.vps.app.util.StringTools;
import org.vps.app.util.keyValue;
import org.vps.service.mgr.VpsCMS;
import org.vps.service.proxy.AuthorizationInfo;
import org.vps.service.proxy.AuthorizationParse;
import org.vps.service.workdirect.INotifyConnectComplete;
import org.vps.service.workdirect.ISorks5ProxyDirect;
import org.vps.service.workdirect.Socks5DirectHandler;

import edu.hziee.common.lang.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;

public class Socks5ProxyDirectAdapter implements INotifyConnectComplete, ISorks5ProxyDirect {
    public static final Logger LOGGER = LoggerFactory.getLogger(Socks5ProxyDirectAdapter.class);
 
    private ISocks5ProxyHandler handler;
    
    private Socks5DirectHandler directHandler;
    
    private Object defaultSocks5InitialRequest;
    private Object defaultSocks5PasswordAuthRequest;
    public static int STEP_INIT = 0;//STEP_INIT
    public static int STEP_PASS = 1;//STEP_PASS
    public static int STEP_CONNECT = 2;//STEP_CONNECT
    public static int STEP_CONNECT_SUC = 3;//STEP_CONNECT_SUC
    public static int STEP_CONNECT_ERR = 4;//STEP_CONNECT_ERR

    public Socks5ProxyDirectAdapter(ISocks5ProxyHandler handler, LogProxySession logProxySession) {
        this.handler = handler;
    }
    
    public boolean connect(String host, int port) {
        VpsCMS.getDirectBoootstrap().connectAsync(host, port, this);
        return true;
    }
    
    public boolean transferData(Object data) {
        if (null != directHandler) {
            directHandler.send(data);
            return true;
        }
        return false;
    }

    public void proxyChannelClose() {
        try {
            if (null != directHandler) directHandler.close();
        } catch (Exception e) {
            LOGGER.error("close exception. url: {}, msg: {}.", new Object[]{"", e.getMessage(), e});
        }
    }

    public void recvResponse(ByteBuf data) {
        handler.socks5ResponseData(data);
    }
    
    public void readAsync() {
        directHandler.readAsync();
    } 

    @Override
    public void ConnectFinish(Object handler, boolean isSuc) {
        if (isSuc) {
            directHandler = (Socks5DirectHandler)handler;
            directHandler.match(this);
            sendPer(STEP_INIT, defaultSocks5InitialRequest);
        } else {
            this.handler.socks5ResponsePasswordAuthErr();
            notifyClose();
        }
    }

    @Override
    public void recv(ByteBuf data) {
        handler.socks5ResponseData(data);
    }

    @Override
    public void notifyClose() {
        handler.socks5ChannelClose();
    }

    @Override
    public void readComplete() {
        if (handler.socks5ResponseWritable()) {
            directHandler.readAsync();
        } else {
//            LOGGER.info("socks5 proxy 无法写数据");
        }
    }

	public boolean connectSelect(String name, String pwd) {
		// 选择代理服务器进行连接
    	AuthorizationInfo info = AuthorizationParse.parse(name, pwd);
        String hostPort = ProxyTransferRule.get(info.getUsername(), info.getUid());
        if (!StringUtil.isBlank(hostPort)) {
            String []splitHostPort = hostPort.split(":");
            String host = splitHostPort[0];
            int port = StringTools.String2Int(splitHostPort[1], 0);
            if (port == 0) {
            	return false;
            }
            ProxyTransferRule.set(info.getUsername(), info.getUid(), hostPort);
            VpsCMS.getSocks5DirectBootstrap().connectAsync(host, port, this);
        } else {
            ArrayList<keyValue<String, Integer>> dstProxyServerlist = VpsCMS.getConfig().getListProxySocks5Host();
            if (dstProxyServerlist == null) {
                return false; 
            }
            keyValue<String, Integer> keyValue = dstProxyServerlist.get((int)(Math.random()*dstProxyServerlist.size()));
            ProxyTransferRule.set(info.getUsername(), info.getUid(), keyValue.getKey()+":"+keyValue.getValue());
            VpsCMS.getSocks5DirectBootstrap().connectAsync(keyValue.getKey(), keyValue.getValue(), this); 
        }
        return true;
	}

	public void setDefaultSocks5InitialRequest(Object defaultSocks5InitialRequest) {
		this.defaultSocks5InitialRequest = defaultSocks5InitialRequest;
	}

	public void setDefaultSocks5PasswordAuthRequest(Object defaultSocks5PasswordAuthRequest) {
		this.defaultSocks5PasswordAuthRequest = defaultSocks5PasswordAuthRequest;
	}

	@Override
	public void sendPer(int step , Object msg) {
	    switch (step) {
        case 0:
            transferData(defaultSocks5InitialRequest);
            break;
        case 1:
            transferData(defaultSocks5PasswordAuthRequest);
            break;
        case 2:
            if (!((DefaultSocks5PasswordAuthResponse)msg).status().isSuccess()) {
                handler.socks5ResponsePasswordAuthErr();
                notifyClose();
            } else {
                handler.socks5ResponsePasswordAuthSuc();
            }
            break;
        case 3:
            this.handler.socks5ResponseCmdConnectSuc();
            break;
        case 4:
            this.handler.socks5ResponseCmdConnectErr();
            break;
        default:
            break;
        }
	} 

}
