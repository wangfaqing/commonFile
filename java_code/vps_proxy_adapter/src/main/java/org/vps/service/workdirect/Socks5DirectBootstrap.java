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
import io.netty.handler.codec.socksx.v5.Socks5AddressEncoder;
import io.netty.handler.codec.socksx.v5.Socks5ClientEncoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponseDecoder;

public class Socks5DirectBootstrap {
    
	public static final Logger logger = LoggerFactory.getLogger(Socks5DirectBootstrap.class);

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
                	ch.pipeline().addFirst(new Socks5ClientEncoder(Socks5AddressEncoder.DEFAULT));
                    ch.pipeline().addLast("socks5InitialResponseDecoder", new Socks5InitialResponseDecoder());
                    ch.pipeline().addLast(new Socks5DirectHandler());
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
                        Socks5DirectHandler handler = future.channel().attr(Socks5DirectHandler.attrKey).get();
                        callback.ConnectFinish(handler, true);
                    } else {
                        callback.ConnectFinish((Socks5DirectHandler)null, false);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("connect [{}:{}] exception. msg: {}", new Object[]{host, port, e.getMessage(), e});
            callback.ConnectFinish((Socks5DirectHandler)null, false);
        }
	}
	
	public void release() {
	    workerGroup.shutdownGracefully();
	}
	
}
