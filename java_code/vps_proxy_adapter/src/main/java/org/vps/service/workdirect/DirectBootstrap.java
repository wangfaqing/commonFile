package org.vps.service.workdirect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DirectBootstrap {
    
	public static final Logger logger = LoggerFactory.getLogger(DirectBootstrap.class);

	private EventLoopGroup workerGroup;
	private Bootstrap b;
	
	public void init() {
	    workerGroup = new NioEventLoopGroup();

        try {
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {                    
                    ch.pipeline().addLast(new DirectHandler());
                }
            });

        } catch(Exception e){
            logger.error(e.getMessage(), e);
        }
	}
	
	/*
	 * 连接指定的服务器器，并导步回传连接状态
	 */
	public void connectAsync(String host, int port, final INotifyConnectComplete callback) {
	    try {
	        b.connect(host, port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        if (logger.isInfoEnabled()) logger.info("线程id: {}", Thread.currentThread().getId());
                        DirectHandler handler = future.channel().attr(DirectHandler.attrKey).get();
                        callback.ConnectFinish(handler, true);
                    } else {
                        callback.ConnectFinish((DirectHandler)null, false);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("connect [{}:{}] exception. msg: {}", new Object[]{host, port, e.getMessage(), e});
            callback.ConnectFinish((DirectHandler)null, false);
        }
	}
	
	public void release() {
	    workerGroup.shutdownGracefully();
	}
	
}
