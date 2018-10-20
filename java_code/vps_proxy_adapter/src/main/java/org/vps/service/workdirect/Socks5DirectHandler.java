package org.vps.service.workdirect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.service.proxy.socks5.Socks5ProxyDirectAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponseDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponseDecoder;
import io.netty.util.AttributeKey;

public class Socks5DirectHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(DirectHandler.class);
    
    public static AttributeKey<Socks5DirectHandler> attrKey = AttributeKey.valueOf("Socks5DirectHandler");
    
    private ChannelHandlerContext ctx;
    
    private ISorks5ProxyDirect socks5ProxyDirect;
	
    private String beferHandleName = "socks5InitialResponseDecoder";
    
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(attrKey).set(this);
        this.ctx = ctx;
    };
    
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (null != socks5ProxyDirect) {
            socks5ProxyDirect.notifyClose();
        }
	};
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    ctx.channel().config().setAutoRead(false);
	    ctx.channel().read();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof DefaultSocks5InitialResponse) {
			removeBeferHandlerAndAddNew("socks5PasswordAuthResponseDecoder", new Socks5PasswordAuthResponseDecoder());
			socks5ProxyDirect.sendPer(Socks5ProxyDirectAdapter.STEP_PASS, msg);
		} else if (msg instanceof DefaultSocks5PasswordAuthResponse) {
			removeBeferHandlerAndAddNew("socks5CommandResponseDecoder", new Socks5CommandResponseDecoder());
			socks5ProxyDirect.sendPer(Socks5ProxyDirectAdapter.STEP_CONNECT, msg);
		} else if (msg instanceof DefaultSocks5CommandResponse) {
			ctx.pipeline().remove(beferHandleName);
			beferHandleName="";
			if (((DefaultSocks5CommandResponse)msg).status().isSuccess()) {
				socks5ProxyDirect.sendPer(Socks5ProxyDirectAdapter.STEP_CONNECT_SUC, msg);
			} else {
				socks5ProxyDirect.sendPer(Socks5ProxyDirectAdapter.STEP_CONNECT_ERR, msg);
			}
		} else if (msg instanceof ByteBuf) {
	        channelRead0(ctx, (ByteBuf) msg);
	    } else {
	        ctx.channel().close();
	    }
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	    socks5ProxyDirect.readComplete();
	};
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		ctx.channel().close();
	}
	
	private void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("from: {}, 收到响应数据, size: {}", new Object[]{ctx.channel().remoteAddress().toString(), msg.readableBytes()});
            }
            socks5ProxyDirect.recv(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
	}
    
	public boolean match(ISorks5ProxyDirect sorks5ProxyDirect) {
        this.socks5ProxyDirect = sorks5ProxyDirect;
	    return true;
	}
	
	public void send(Object data) {
        ctx.writeAndFlush(data);
    }
	
	public void close() {
	    ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	} 
	
	public void readAsync() {
	    ctx.channel().read();
	} 
	
	private void removeBeferHandlerAndAddNew(String newName, ChannelHandler handler) {
        ctx.pipeline().remove(beferHandleName);
        ctx.pipeline().addFirst(newName, handler);
        beferHandleName = newName;
    }
}
