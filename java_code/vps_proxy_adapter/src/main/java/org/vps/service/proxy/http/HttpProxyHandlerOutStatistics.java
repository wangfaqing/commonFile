package org.vps.service.proxy.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
  
/**
 * 
 * 代理写封装
 * @author <a href="mailto:wangyaofeng@zhexinit.com" >王垚丰</a>
 * @version 1.0.0
 */
public class HttpProxyHandlerOutStatistics extends ChannelOutboundHandlerAdapter {  
	@SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpProxyHandlerOutStatistics.class);
	private AttributeKey<HttpProxyAdapter> attrKeyHPA = AttributeKey.valueOf("HttpProxyAdapter");
	
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
	    HttpProxyAdapter httpProxyAdapter = ctx.attr(attrKeyHPA).get();
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            // 记录发送的字节数
            if (httpProxyAdapter != null) httpProxyAdapter.setSendBytesCount(buf.readableBytes());
            ctx.write(buf, promise);
        } else if (msg instanceof String) {
            String str = (String) msg;
            ByteBuf buf = ctx.alloc().buffer(str.length());
            buf.writeBytes(str.getBytes());
            // 记录发送的字节数
            if (httpProxyAdapter != null) httpProxyAdapter.setSendBytesCount(buf.readableBytes());
            ctx.write(buf, promise);
        } else {
            ctx.write(msg, promise);
        }
    }
	
} 