package org.vps.service.workdirect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class DirectHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(DirectHandler.class);
    
    public static AttributeKey<DirectHandler> attrKey = AttributeKey.valueOf("DirectHandler");
    
    private ChannelHandlerContext ctx;
    
    private IHttpProxyDirect socks5ProxyDirect;
	
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
//	    this.ctx = ctx;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    if (msg instanceof ByteBuf) {
	        channelRead0(ctx, (ByteBuf) msg);
	    } else {
	        ctx.close();
	    }
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	    socks5ProxyDirect.readComplete();
	};
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
		ctx.close();
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
    
	public boolean match(IHttpProxyDirect sorks5ProxyDirect) {
        this.socks5ProxyDirect = sorks5ProxyDirect;
	    return true;
	}
	
	public void send(ByteBuf data) {
        ctx.writeAndFlush(data);
    }
	
	public void close() {
	    ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	} 
	
	public void readAsync() {
	    ctx.channel().read();
	} 
}
