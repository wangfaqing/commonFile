package org.vps.service.proxy.handler.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
  
/**
 * 
 * 支持  Bytebuf 写缓冲
 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
 * @version 1.0.0
 */
public class WriteBufferHandler extends ChannelDuplexHandler {  
	@SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteBufferHandler.class);
	
	IWriteBuffer writeBuffer = null;
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
	    writeBuffer = new WriteBuffer(ctx);
	    ctx.fireChannelRegistered();
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
	    writeBuffer.release();
	    ctx.fireChannelUnregistered();
	}
	
	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
	    writeBuffer.write();
	    ctx.fireChannelWritabilityChanged();
	}
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
	    if (!(msg instanceof ByteBuf)) {
	        throw new IllegalArgumentException("only support ByteBuf!");
	    }
        writeBuffer.write((ByteBuf)msg, promise);
    }
	
} 