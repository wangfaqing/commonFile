package org.vps.service.proxy.httppublic.ordercheck;

import edu.hziee.common.lang.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jodd.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.cms.AppInstance;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.redis.ProxyTransferRule;
import org.vps.app.util.StringTools;
import org.vps.app.util.keyValue;
import org.vps.service.mgr.VpsCMS;
import org.vps.service.proxy.AuthorizationInfo;
import org.vps.service.proxy.ProxyStaticVar;
import org.vps.service.proxy.httppublic.ordercheck.bean.PublicProxyParam;
import org.vps.service.proxy.handler.common.IUpStreamDowmStreamStatistics;
import org.vps.service.proxy.httppublic.*;
import org.vps.service.workdirect.DirectHandler;
import org.vps.service.workdirect.IHttpProxyDirect;
import org.vps.service.workdirect.INotifyConnectComplete;

import java.net.URL;
import java.util.*;

public class HttpProxyAdapter implements IHttpProxyDirect, INotifyConnectComplete, IUpStreamDowmStreamStatistics {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyAdapter.class);
	private static final String PROXY_USER = "ly";
	private static final String PROXY_KEY = "Fh8caXRpoBxwNaBV";
	
    private final static String header_proxy_connect = "proxy-connection";
    private final static String header_connect = "connection";
    
	private Step step = Step.NONE;
	
	private ByteBuf requestData = null;
	private HttpRequestHeader requestHeader = null;
	
	private PublicProxyParam proxyParam = null;

    private AuthorizationInfo authorizationInfo = null;
	private LogProxySession logProxySession;
	
	private IHttpProxyHandler handler;
	
	private DirectHandler directHandler;
	private boolean isCountOfConn = false;
	
	public HttpProxyAdapter(IHttpProxyHandler handler, LogProxySession logProxySession) {
	    super();
	    this.handler = handler;
		requestData = Unpooled.buffer();
		requestHeader = new HttpRequestHeader();		
		this.logProxySession = logProxySession;
		logProxySession.setSessionID(0);
	}
	 
	private void release() {
		if (requestData != null) {
			requestData.release();
		}
	} 
	
	public AuthorizationInfo getAuthorizationInfo() {
	    return authorizationInfo;
	} 
	
	private String getUrlFile(String strUrl) {
		try {
			URL url = new URL(strUrl);
			return url.getFile();
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("get url file: " + strUrl + ", " + e.getMessage(), e);
			}
		}
		return "/";
	}
	
	@SuppressWarnings("unused")
    private ByteBuf CreateSendBufferV2(HttpRequestHeader recvHeader) {
        keyValue<String, String> connectionHeader = null;
        keyValue<String, String> proxyConnectionHeader = null;

        ByteBuf buffer = Unpooled.buffer();

        String file = getUrlFile(requestHeader.getUri());

        String strHttpPro = recvHeader.getMethon() + " " + file + " " + recvHeader.getVersion() + "\r\n";
        buffer.writeBytes(strHttpPro.getBytes());

        Iterator<keyValue<String, String>> iterator = recvHeader.getHeaders().iterator();
        while (iterator.hasNext()) {
            keyValue<String, String> header = iterator.next();
            if (header.getKey().toLowerCase().equals(header_connect)) {
                connectionHeader = header;
                continue;
            } else if (header.getKey().toLowerCase().equals(header_proxy_connect)) {
                proxyConnectionHeader = header;
                continue;
            }
        }

        for(int i = 0; i<recvHeader.getHeaders().size(); i++) {
            String strHeaderLine="";
            if (recvHeader.getHeaders().get(i).getKey().toLowerCase().equals(header_proxy_connect)) {
                if (connectionHeader == null) {
                    strHeaderLine = "Connection";
                    strHeaderLine += ": " + recvHeader.getHeaders().get(i).getValue() + "\r\n";
                }
            } else if (recvHeader.getHeaders().get(i).getKey().toLowerCase().equals(header_connect)) {
                if (proxyConnectionHeader != null) {
                    strHeaderLine = recvHeader.getHeaders().get(i).getKey();
                    if (connectionHeader.getValue().toLowerCase().equals("close")) {
                        strHeaderLine += ": " + connectionHeader.getValue() + "\r\n";
                    } else if (proxyConnectionHeader.getValue().toLowerCase().equals("close")) {
                        strHeaderLine += ": " + proxyConnectionHeader.getValue() + "\r\n";
                    } else {
                        strHeaderLine += ": " + recvHeader.getHeaders().get(i).getValue() + "\r\n";
                    }
                } else {
                    strHeaderLine = recvHeader.getHeaders().get(i).getKey();
                    strHeaderLine += ": " + recvHeader.getHeaders().get(i).getValue() + "\r\n";
                }
            } else{
                strHeaderLine = recvHeader.getHeaders().get(i).getKey();
                strHeaderLine += ": " + recvHeader.getHeaders().get(i).getValue() + "\r\n";
            }
            buffer.writeBytes(strHeaderLine.getBytes());
        }

        buffer.writeBytes("\r\n".getBytes());
        return buffer;
    }
    
	private boolean transferProxyData() {
	    //TODO: 转换鉴权功能
	    return transferProxyData(requestHeader, requestData);
//        ByteBuf buf = requestData.copy(0, requestData.readableBytes());
//        directHandler.send(buf);
//        return true;
    } 
    
	private ByteBuf CreateSendBuffer(HttpRequestHeader recvHeader) {
		ByteBuf buffer = Unpooled.buffer();
		
		String file = requestHeader.getUri();
		
		String strHttpPro = recvHeader.getMethon() + " " + file + " " + recvHeader.getVersion() + "\r\n";
		buffer.writeBytes(strHttpPro.getBytes());
		
		for(int i = 0; i<recvHeader.getHeaders().size(); i++) {
			String strHeaderLine = recvHeader.getHeaders().get(i).getKey();
			strHeaderLine += ": " + recvHeader.getHeaders().get(i).getValue() + "\r\n";
			buffer.writeBytes(strHeaderLine.getBytes());
		}
		// 添加Proxy-Authorization header
		String strProxyAuthHeaderLine = "Proxy-Authorization: Basic " + Base64.encodeToString(PROXY_USER + ":" + ProxyNewPwd.build(PROXY_KEY, Integer.toString(3), UUID.randomUUID().toString(), -1, -1, -1));
		buffer.writeBytes(strProxyAuthHeaderLine.getBytes());
		buffer.writeBytes("\r\n".getBytes());
		
		buffer.writeBytes("\r\n".getBytes());
		return buffer;
	}
	
	private boolean transferProxyData(HttpRequestHeader recvHeader, ByteBuf recvData) {
	    ByteBuf buffer = CreateSendBuffer(recvHeader);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(" send data1 len: " + buffer.writerIndex() + " " +recvHeader.getUri());
		}
		
		directHandler.send(buffer);
		
		if (recvData.writerIndex() - recvHeader.getHeaderLen() > 2) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("refer " + " send data2 len: " + (recvData.writerIndex()-recvHeader.getHeaderLen()-2) + " " +recvHeader.getUri());
			}

			ByteBuf buffer2 = Unpooled.buffer(recvData.writerIndex()-recvHeader.getHeaderLen()-2);
			buffer2.writeBytes(recvData, recvHeader.getHeaderLen()+2, recvData.writerIndex()-recvHeader.getHeaderLen()-2);
			directHandler.send(buffer2);
		}
		
		return true;
	} 
	
	public void proxyChannelClose() {
		try {
            release();
		    logProxySession.setDisconnectTimestamp(new Date().getTime());
	        if (logProxySession.getRecvByteCounts() != 0 || logProxySession.getSendByteCounts() != 0) {
	            AppInstance.getLogExecution().Add(logProxySession);
	        }
	        AppInstance.getOrderInfoManage().disconnectOrderInfo(proxyParam);
	        if (logProxySession.getRecvByteCounts() + logProxySession.getSendByteCounts() > 0) {
	            //更新流量
	            AppInstance.getOrderInfoManage().updateOrderInfoFlow(proxyParam, new Long((logProxySession.getRecvByteCounts() + logProxySession.getSendByteCounts())));
	        }
		} catch (Exception e) {
		    LOGGER.error(e.getMessage(), e);
		}
	} 
	
    private void proxyAuthenticationResponse() {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("--- need authentication.");
	    String strResponse = String.format("%s %s\r\n%s\r\n%s\r\n\r\n", 
	        requestHeader.getVersion(), "407 Unauthorized", "Proxy-Authenticate: Basic realm=\".\"", "Content-Length: 0");
	    handler.httpResponse(strResponse);
	} 
	
    private void connectResponse() {
        String strResponse = requestHeader.getVersion() + " 200 " + "Blind-Connection Established\r\n";
        strResponse += "\r\n";
        handler.httpResponse(strResponse);
    }
    
	private void connectErrorResponse() {
	    String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), "502 Connection Failed");
	    handler.httpResponseError(strResponse);
	}

    private void badRequestResponse() {
        String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), "400 Bad Request");
        handler.httpResponseError(strResponse);
    }

    private void paramCheckResponse(String msg) {
        handler.httpResponseError(msg);
    }

    private void errorResponse(PublicProxyParam proxyParam) {
        if(!StringUtils.isEmpty(proxyParam.getErrorMsg())) {
            String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), proxyParam.getErrorMsg());
            handler.httpResponseError(strResponse);
        }
    }
	
	public void readable(ByteBuf buf) {
		try {
			//https请求，并且进行转发流程，直接转发
			if (step == Step.TUNNEL) {
			    directHandler.send(buf);
				return ;
			}
			
			//http长连接，之前的请求数据清空，重新接收数据
			if (HttpRequest.isRequestComplete(requestData, requestHeader)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("清空上次转发数据 , url:{}", requestHeader.getUri());
				}
				requestData.clear();
				requestHeader = new HttpRequestHeader();
			}

			requestData.writeBytes(buf);
			buf.release();
			
			if (!HttpRequest.isRequestComplete(requestData, requestHeader)) {
			    return; 
			}
			
			logProxySession.setRequestTime(logProxySession.getRequestTime() + 1);
			
		    if (StringUtil.isBlank(logProxySession.getUrl())) logProxySession.setUrl(requestHeader.getUri());
		    printHeader();
		    HttpProxyHeaderParse.parse(this);
            if (proxyParam == null) {
                proxyAuthenticationResponse();
                return;
            }
            if (!StringUtil.isBlank(proxyParam.getOrderId()) && !StringUtil.isBlank(proxyParam.getSign()) && (proxyParam.getTime()!=0)) {
                AppInstance.getOrderInfoManage().getInfo(proxyParam);
                errorResponse(proxyParam);
            }else{
                //proxyAuthenticationResponse();
                String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), "601 Necessary Parameter Is Empty");
                paramCheckResponse(strResponse);
                return;
            }

		    if (step == Step.CONNECTED && !requestHeader.getMethon().toUpperCase().equals("CONNECT")) {
                //http长连接，之前已连接，可直接转发数据, HttpProxyHeaderParse.parse 删除第二次收到的Proxy-Authorization头
                if (!transferProxyData( requestHeader, requestData)) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("vps proxy 转发数据失败: {}", requestHeader.getUri());
                    }
                    
                    connectErrorResponse();
                }
                return ;
		    }


//            if (StringUtil.isBlank(proxyParam.getSecret())) {
//                return;
//            }


		    // 加ProxySessionCheck
            if (!PublicAuthorizationCheckForHttp.isPass(getProxyParam(), getLogProxySession())) {
                if (getLogProxySession().getUserType() == ProxyStaticVar.USER_TYPE_BLACKLIST) {
                    badRequestResponse();
                    return ;
                }
                if (getProxyParam() == null) { //proxyParam
                    proxyAuthenticationResponse();
                } else {
                    //badRequestResponse();
                    errorResponse(proxyParam);
                }
                return ;
            }

//            PublicUser publicUser = AppInstance.getPublicUsers().getPublicUser(proxyParam.getUser());
//            if (publicUser == null) {
//                badRequestResponse();
//                return ;
//            }
//            // rid
//            if (proxyParam.getRid() <= 0 || proxyParam.getRid() > publicUser.getUidLimit()) {
//                badRequestResponse();
//                return ;
//            }
//            // 并发连接数, redis get connectused, if not limit, inr 1
//            if (!AppInstance.getPublicUserOrderMgr().ConnPublicUser(publicUser)) {
//                badRequestResponse();
//                return ;
//            }

            //增加一个连接
            //AppInstance.getOrderInfoManage().connectOrderInfo(proxyParam);

            isCountOfConn = true;

            //校验订单有效期、流量及并发数
            if(!AppInstance.getOrderInfoManage().checkOrderStatus(proxyParam)) {
                //badRequestResponse();
                errorResponse(proxyParam);
                return;
            }
            
            // req/flow redis check orderid is not limit, or get new orderid and check            
//            if (!AppInstance.getPublicUserOrderMgr().checkPublicUser(publicUser)) {
//                badRequestResponse();
//                return ;
//            }
            
            //proxyParam
            // 选择代理服务器进行连接
            String hostPort = ProxyTransferRule.get(proxyParam.getOrderId(), proxyParam.getSecret());
            if (!StringUtil.isBlank(hostPort)) {
                String []splitHostPort = hostPort.split(":");
                String host = splitHostPort[0];
                int port = StringTools.String2Int(splitHostPort[1], 0);
                if (port == 0) {
                    badRequestResponse(); return;
                }
                ProxyTransferRule.set(proxyParam.getOrderId(), proxyParam.getSecret(), hostPort);
                VpsCMS.getDirectBoootstrap().connectAsync(host, port, this);
            } else {
                ArrayList<keyValue<String, Integer>> dstProxyServerlist = VpsCMS.getConfig().getListProxyHttpHost();
                if (dstProxyServerlist == null) {
                    badRequestResponse(); return;
                }
                keyValue<String, Integer> keyValue = dstProxyServerlist.get((int)(Math.random()*dstProxyServerlist.size()));
                ProxyTransferRule.set(proxyParam.getOrderId(), proxyParam.getSecret(), keyValue.getKey()+":"+keyValue.getValue());
                VpsCMS.getDirectBoootstrap().connectAsync(keyValue.getKey(), keyValue.getValue(), this);
            }
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("readable error: " + e.getMessage(), e);
			}
		}
	}
    
    private void printHeader() {
        if (LOGGER.isInfoEnabled()) {
            String print = String.format("%s %s %s\r\n", requestHeader.getMethon(), requestHeader.getUri(), requestHeader.getVersion());
            List<keyValue<String, String>> headers = requestHeader.getHeaders();
            Iterator<keyValue<String, String>> iterator = headers.iterator();
            while (iterator.hasNext()) {
                keyValue<String, String> header = iterator.next();
                print += header.getKey() + " " + header.getValue() + "\r\n";
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
    
    public void setSendBytesCount(int count) {
        logProxySession.setSendByteCounts(logProxySession.getSendByteCounts() + count);
    }
    
    public PublicProxyParam getProxyParam() {
        return proxyParam;
    }

    public void setProxyParam(PublicProxyParam proxyParam) {
        this.proxyParam = proxyParam;
    }

    @Override
    public void ConnectFinish(Object handler, boolean isSuc) {
        if (isSuc) {
            directHandler = (DirectHandler)handler;
            directHandler.match(this);
            if (requestHeader.getMethon().toUpperCase().equals("CONNECT")) {
                step = Step.TUNNEL;
                connectResponse();
            } else {
                step = Step.CONNECTED;
                transferProxyData();
            }
        } else {
            String strResponse = String.format("%s %s\r\n\r\n", requestHeader.getVersion(), "502 Proxy Connection Failed");
            this.handler.httpResponseError(strResponse);
        }
        
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
    public void upStreamInc(int size) {
        logProxySession.setRecvByteCounts(logProxySession.getRecvByteCounts() + size);
    }

    @Override
    public void downStreamInc(int size) {
        logProxySession.setSendByteCounts(logProxySession.getSendByteCounts() + size);
    }
    
    public static class Step {
        public final static Step NONE       = new Step(0);
        public final static Step CONNECTED  = new Step(1);
        public final static Step TUNNEL     = new Step(2);
        
        @SuppressWarnings("unused")
        private int step = 0;
        public Step(int step) {
            this.step = step;
        }
    }
    
}
