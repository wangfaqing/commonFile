package org.vps.service.proxy.httppublic;

import java.net.InetSocketAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.LogProxySession;
import org.vps.app.util.LanIP;
import org.vps.service.mgr.VpsCMS;
import org.vps.service.proxy.ProxyStaticVar;
import org.vps.service.proxy.handler.common.UpStreamDowmStreamStatisticsHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.vps.service.proxy.httppublic.ordercheck.HttpProxyAdapter;
  
  
/** 
 * Handler implementation for the echo server. 
 */  

public class HttpProxyHandler extends ChannelInboundHandlerAdapter implements IHttpProxyHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyHandler.class);
	
	private AttributeKey<HttpProxyAdapter> attrKeyHPA = AttributeKey.valueOf("HttpProxyAdapter");
	private HttpProxyAdapter httpProxyAdapter = null;
	private ChannelHandlerContext ctx;
	private HttpProxyContext context;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    this.ctx = ctx;
	    
	    context = new HttpProxyContext();
        
        LogProxySession log = new LogProxySession(VpsCMS.getConfig().getVpsLocalIP(), httpGetRemoteAddr());
        log.setUserType(ProxyStaticVar.USER_TYPE_OTHER);
        log.setPass(ProxyStaticVar.UNPASS);
        log.setSessionID(0);
        log.setConnectTimestamp(new Date().getTime());
        
        context.setLogProxySession(log);
	    
	    httpProxyAdapter =  new HttpProxyAdapter(this, context.getLogProxySession());// new HttpProxyAdapter(this);
		ctx.attr(attrKeyHPA).set(httpProxyAdapter);
		ctx.attr(UpStreamDowmStreamStatisticsHandler.statistics).set(httpProxyAdapter);
	}
		
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		httpProxyAdapter.proxyChannelClose();
		ctx.attr(attrKeyHPA).set(null);
	}
	
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        httpProxyAdapter.readable((ByteBuf)msg);
    }  
  
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            String remoteAddress = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
            if (LanIP.isAliyumIp(remoteAddress)) {
                return ;
            }
            LOGGER.warn("exceptionCaught src: {}, msg: {}", new Object[]{remoteAddress, cause.getMessage(), cause});
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    	ctx.close();
    } 
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.close();
            }
        }
    }
    
    @Override
    public String httpGetRemoteAddr() {
        return ctx.channel().remoteAddress().toString();
    }

    @Override
    public void httpChannelClose() {
        ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void httpResponseError(String response) {
        ctx.channel().writeAndFlush(response);
        httpChannelClose();
    }

    @Override
    public void httpResponse(String response) {
        ctx.channel().writeAndFlush(response);
    }

    @Override
    public void httpResponse(ByteBuf data) {
        ctx.channel().writeAndFlush(data);
    }
    
} 