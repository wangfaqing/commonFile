package org.vps.service.proxy.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;  
  
  
/** 
 * Handler implementation for the echo server. 
 */  

public class HttpProxyHandler extends ChannelInboundHandlerAdapter implements IHttpProxyHandler {  
	@SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyHandler.class);
	
	private AttributeKey<HttpProxyAdapter> attrKeyHPA = AttributeKey.valueOf("HttpProxyAdapter");
	private HttpProxyAdapter httpProxyAdapter = null;
	private ChannelHandlerContext ctx;
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	    this.ctx = ctx;
	    httpProxyAdapter = new HttpProxyAdapter(this);
		ctx.attr(attrKeyHPA).set(httpProxyAdapter);
	}
		
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		httpProxyAdapter.proxyChannelClose();
		ctx.attr(attrKeyHPA).set(null);
	}
	
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        httpProxyAdapter.readable((ByteBuf)msg);
    }  
  
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
        if (ctx.channel() != null) {
            if (ctx.channel().remoteAddress() != null) {
                return ctx.channel().remoteAddress().toString();
            }
        }
        return null;
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