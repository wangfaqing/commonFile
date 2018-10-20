package org.vps.service.proxy.http;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.cms.AppInstance;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.redis.ProxyTransferRule;
import org.vps.app.util.StringTools;
import org.vps.app.util.keyValue;
import org.vps.service.mgr.VpsCMS;
import org.vps.service.proxy.AuthorizationCheckForHttp;
import org.vps.service.proxy.AuthorizationInfo;
import org.vps.service.workdirect.DirectHandler;
import org.vps.service.workdirect.IHttpProxyDirect;
import org.vps.service.workdirect.INotifyConnectComplete;
import org.vps.service.workdirect.Socks5DirectHandler;

import edu.hziee.common.lang.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@SuppressWarnings("unused")
public class HttpProxyAdapter implements IHttpProxyDirect, INotifyConnectComplete {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyAdapter.class);
	
	private static final int STEP_NONE     = 0;
    private static final int STEP_CONNECT  = 1;
	private static final int STEP_RECV     = 2;
	
	private int step = STEP_NONE;
    private DirectHandler directHandler;
	private ByteBuf requestData = null;
	private HttpRequestHeader requestHeader = null;
	
	private AuthorizationInfo authorizationInfo = null;
	private LogProxySession logProxySession;
	
	private IHttpProxyHandler handler;
	
	public HttpProxyAdapter(IHttpProxyHandler handler) {
	    this.handler = handler;
		requestData = Unpooled.buffer();
		requestHeader = new HttpRequestHeader();		

		
		logProxySession = new LogProxySession(VpsCMS.getConfig().getVpsLocalIP(), handler.httpGetRemoteAddr());
		logProxySession.setConnectTimestamp(new Date().getTime());
	}
	 
	private void release() {
		if (requestData != null) {
			requestData.release();
		}
	} 
	
	private boolean transferProxyData() {
	    ByteBuf buf = requestData.copy(0, requestData.readableBytes());
	    directHandler.send(buf);
	    return true;
	} 
	
	public void proxyChannelClose() {
		try {
		    logProxySession.setDisconnectTimestamp(new Date().getTime());
	        if (logProxySession.getRecvByteCounts() != 0) {
	            //AppInstance.getLogExecution().Add(logProxySession);
	        }
	        
			release();
			directHandler.close();
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("close " + requestHeader.getUri() + e.getMessage());
			}
		}
	} 
    
	private void proxyAuthenticationResponse() {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("--- need authentication.");
        String strResponse = String.format("%s %s\r\n%s\r\n%s\r\n\r\n", 
            requestHeader.getVersion(), "407 Unauthorized", "Proxy-Authenticate: Basic realm=\".\"", "Content-Length: 0");
        handler.httpResponse(strResponse);
    }
    
    private void badRequestResponse() {
        String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), "400 Bad Request");
        handler.httpResponseError(strResponse);
    }
    
	public void readable(ByteBuf buf) {
		try {
		    // 记录 收到的字节数
//		    setRecvBytesCount(buf.readableBytes());
		    if (step == STEP_NONE) {
		        requestData.writeBytes(buf);
	            buf.release();
	            
	            if (!HttpRequest.isRequestComplete(requestData, requestHeader)) {
	                return; 
	            }
	            
	            if (StringUtil.isBlank(logProxySession.getUrl())) logProxySession.setUrl(requestHeader.getUri());
	            printHeader();
	            HttpProxyHeaderParse.parse(this);
	            
	            // 加ProxySessionCheck
	            if (!AuthorizationCheckForHttp.isPass(this)) {
//	                if (getLogProxySession().getUserType() == ProxyStaticVar.USER_TYPE_BLACKLIST) {
//	                    badRequestResponse();
//	                    return ;
//	                }
	                if (authorizationInfo == null) {
	                    proxyAuthenticationResponse();
	                    requestData.clear();
	                    requestHeader = new HttpRequestHeader();
	                } else {
	                    badRequestResponse();
	                }
	                return ;
	            }
	            
	            // 选择代理服务器进行连接
	            String hostPort = ProxyTransferRule.get(authorizationInfo.getUsername(), authorizationInfo.getUid());
	            if (!StringUtil.isBlank(hostPort)) {
	                String []splitHostPort = hostPort.split(":");
	                String host = splitHostPort[0];
	                int port = StringTools.String2Int(splitHostPort[1], 0);
	                if (port == 0) {
	                    badRequestResponse(); return; 
	                }
	                ProxyTransferRule.set(authorizationInfo.getUsername(), authorizationInfo.getUid(), hostPort);
	                VpsCMS.getDirectBoootstrap().connectAsync(host, port, this);
	            } else {
	                ArrayList<keyValue<String, Integer>> dstProxyServerlist = VpsCMS.getConfig().getListProxyHttpHost();
	                if (dstProxyServerlist == null) {
	                    badRequestResponse(); return; 
	                }
	                keyValue<String, Integer> keyValue = dstProxyServerlist.get((int)(Math.random()*dstProxyServerlist.size()));
	                ProxyTransferRule.set(authorizationInfo.getUsername(), authorizationInfo.getUid(), keyValue.getKey()+":"+keyValue.getValue());
	                VpsCMS.getDirectBoootstrap().connectAsync(keyValue.getKey(), keyValue.getValue(), this); 
	            }
		    } else if (step == STEP_RECV) {
		        //已建立转发机制
		        directHandler.send(buf);
			}
		} catch (Exception e) {
			LOGGER.error("readable error: ", e.getMessage(), e);
		}
	}
    
    private void printHeader() {
        if (LOGGER.isInfoEnabled()) {
            String print = String.format("\r\n\t%s %s %s\r\n", requestHeader.getMethon(), requestHeader.getUri(), requestHeader.getVersion());
            List<keyValue<String, String>> headers = requestHeader.getHeaders();
            Iterator<keyValue<String, String>> iterator = headers.iterator();
            while (iterator.hasNext()) {
                keyValue<String, String> header = iterator.next();
                print += "\t" + header.getKey() + " " + header.getValue() + "\r\n";
            }
            LOGGER.info(print);
        }
    }

    public HttpRequestHeader getRequestHeader() {
        return requestHeader;
    }

    public LogProxySession getLogProxySession() {
        return logProxySession;
    }
    
    private void setRecvBytesCount(int count) {
        logProxySession.setRecvByteCounts(logProxySession.getRecvByteCounts() + count);
    } 
    
    public void setSendBytesCount(int count) {
        logProxySession.setSendByteCounts(logProxySession.getSendByteCounts() + count);
    }
    
    public AuthorizationInfo getProxyAuthorizationInfo() {
        return authorizationInfo;
    }
    
    public void setProxyAuthorizationInfo(AuthorizationInfo proxyAuthorizationInfo) {
        this.authorizationInfo = proxyAuthorizationInfo;
    }

    @Override
    public void recv(ByteBuf data) {
        handler.httpResponse(data);
    }

    @Override
    public void notifyClose() {
        handler.httpChannelClose();
    }

    @Override
    public void readComplete() {
        directHandler.readAsync();
    }

    @Override
    public void ConnectFinish(Object handler, boolean isSuc) {
        if (isSuc) {
            directHandler = (DirectHandler)handler;
            directHandler.match(this);
            step = STEP_RECV;
            transferProxyData();
        } else {
            String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), "502 Proxy Connection Failed");
            this.handler.httpResponseError(strResponse);
        }
    }
    
}
