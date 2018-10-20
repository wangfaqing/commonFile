package org.vps.service.proxy.handler.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
  
/**
 * 
 * 代理写封装
 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
 * @version 1.0.0
 */
public class UpStreamDowmStreamStatisticsHandler extends ChannelDuplexHandler {  
	@SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(UpStreamDowmStreamStatisticsHandler.class);
	
	public static final AttributeKey<IUpStreamDowmStreamStatistics> statistics = AttributeKey.valueOf("IUpStreamDowmStreamStatistics");
	
	IUpStreamDowmStreamStatistics upStreamDowmStreamStatistics = null;
	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	    if (msg instanceof ByteBuf) {
	        // 记录收到的字节数
	        upStreamIncrease(ctx, (ByteBuf)msg);
	    }
        ctx.fireChannelRead(msg);
    }
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            // 记录发送的字节数
            downStreamIncrease(ctx, buf);
            ctx.write(buf, promise);
        } else if (msg instanceof String) {
            String str = (String) msg;
            ByteBuf buf = ctx.alloc().buffer(str.length());
            buf.writeBytes(str.getBytes());
            // 记录发送的字节数
            downStreamIncrease(ctx, buf);
            ctx.write(buf, promise);
        } else {
            ctx.write(msg, promise);
        }
    }
	
	@SuppressWarnings("unused")
    private void downStreamIncrease(ChannelHandlerContext ctx, String msg) {
	    IUpStreamDowmStreamStatistics upStreamDowmStreamStatistics = getUpStreamDowmStreamStatistics(ctx);
        if (upStreamDowmStreamStatistics != null) {
            upStreamDowmStreamStatistics.downStreamInc(msg.length());
        }
	}
	    
	private void downStreamIncrease(ChannelHandlerContext ctx, ByteBuf msg) {
	    IUpStreamDowmStreamStatistics upStreamDowmStreamStatistics = getUpStreamDowmStreamStatistics(ctx);
	    if (upStreamDowmStreamStatistics != null) {
	        upStreamDowmStreamStatistics.downStreamInc(msg.readableBytes());
	    }
	}
	
    private void upStreamIncrease(ChannelHandlerContext ctx, ByteBuf msg) {
        IUpStreamDowmStreamStatistics upStreamDowmStreamStatistics = getUpStreamDowmStreamStatistics(ctx);
        if (upStreamDowmStreamStatistics != null) {
            upStreamDowmStreamStatistics.upStreamInc(msg.readableBytes());
        }
    }
	
    private IUpStreamDowmStreamStatistics getUpStreamDowmStreamStatistics(ChannelHandlerContext ctx) {
        if (upStreamDowmStreamStatistics == null) {
            upStreamDowmStreamStatistics = ctx.attr(statistics).get();
        }
        return upStreamDowmStreamStatistics;
    }
} 