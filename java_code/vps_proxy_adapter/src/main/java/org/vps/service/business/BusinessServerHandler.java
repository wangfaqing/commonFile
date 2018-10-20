package org.vps.service.business;


import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

public class BusinessServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessServerHandler.class);

	private HttpPostRequestDecoder decoder;

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (decoder != null) {
			decoder.cleanFiles();
		}
	}

	public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if (msg instanceof HttpRequest) {
		    HttpRequest request = (HttpRequest) msg;
		    try {
	            URI uri = new URI(request.uri());
	            if (isGet(request)) {
	                switch (uri.getPath()) {
	                case "/test.do":
                        if (sendResponse(ctx, request, test(ctx, request))) return ;
	                    break;
	                default:
	                    break;
	                }    
	            }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
			
			sendResponseNotFoud(ctx, request);
		} else {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("不是一个http请求");
			ctx.channel().close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)throws Exception {
		messageReceived(ctx, msg);
	}

	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                ctx.channel().close();
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("query connect timeout, ip: " + ctx.channel().remoteAddress().toString());
                }
            }
        }
    }
	
	private boolean isGet(HttpRequest request) {
	    if (request.method().equals(HttpMethod.GET)) {
	        return true;
	    }
	    return false;
	}

    private ByteBuf test(ChannelHandlerContext ctx, HttpRequest request) {
	    JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("result", 0);
        jsonObject.addProperty("message", "");
        return Unpooled.copiedBuffer(jsonObject.toString(), CharsetUtil.UTF_8);
    }
    
	private boolean sendResponse(ChannelHandlerContext ctx, HttpRequest request, ByteBuf buf) {
	    if (buf == null) return false;
        // 创建返回对象
        FullHttpResponse encoder = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, buf);

        encoder.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        encoder.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

        // 返回数据
        ChannelFuture future = ctx.channel().writeAndFlush(encoder);
        future.addListener(ChannelFutureListener.CLOSE);
        
        return true;
	}
	
	private boolean sendResponseNotFoud(ChannelHandlerContext ctx, HttpRequest request) {
        // 创建返回对象
        FullHttpResponse encoder = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND);
        // 返回数据
        ChannelFuture future = ctx.channel().writeAndFlush(encoder);
        future.addListener(ChannelFutureListener.CLOSE);
        
        return true;
	}
    
}
