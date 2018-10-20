package org.vps.service.listen;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vps.service.proxy.handler.common.UpStreamDowmStreamStatisticsHandler;
import org.vps.service.proxy.handler.common.WriteBufferHandler;
import org.vps.service.proxy.http.HttpProxyHandler;
import org.vps.service.proxy.http.HttpProxyHandlerOutStatistics;
import org.vps.service.proxy.socks5.Socks5ProxyHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5AddressEncoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/** 
  * @author  wangyaofeng E-mail: wangyaofeng@zhexinit.com
  * @date 创建时间：2016年5月17日 下午6:08:47 
  * @version 1.0 
  */
@Service("serviceentry")
public class ServiceEntry {
	private static final Logger logger = LoggerFactory.getLogger(ServiceEntry.class);
   	
    public static void httpProxy(String strHost, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 写120秒超时
                            ch.pipeline().addFirst("idleStateHandler", new IdleStateHandler(0, 120, 0));
                            // 写缓冲，只支持ByteBuf
                            ch.pipeline().addLast("writeBuffer", new WriteBufferHandler());
                            // 统计流入，流出字存数，只统计ByteBuf，其它不统计
                            ch.pipeline().addLast("upStreamDowmStreamStatistics", new UpStreamDowmStreamStatisticsHandler());
                            ch.pipeline().addLast(new HttpProxyHandlerOutStatistics());
                            ch.pipeline().addLast(new HttpProxyHandler());
                            
                        }
                    }).option(ChannelOption.SO_BACKLOG, 256).childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 64*1024);
            b.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256*1024);
            
            ChannelFuture f = b.bind(strHost, port);
            f.addListener(new MyChannelFutureListener(strHost, port));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public static void socks5Proxy(String strHost, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 写60秒超时
                            ch.pipeline().addFirst("idleStateHandler", new IdleStateHandler(0, 60, 0));
                            // 写缓冲，只支持ByteBuf
                            ch.pipeline().addLast("writeBuffer", new WriteBufferHandler());
                            // 统计流入，流出字存数，只统计ByteBuf，其它不统计
                            ch.pipeline().addLast("upStreamDowmStreamStatistics", new UpStreamDowmStreamStatisticsHandler());
                            ch.pipeline().addLast("Socks5ServerEncoder", new Socks5ServerEncoder(Socks5AddressEncoder.DEFAULT));
                            ch.pipeline().addLast("socks5InitialRequestDecoder", new Socks5InitialRequestDecoder());
                            ch.pipeline().addLast(new Socks5ProxyHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 256).childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 64*1024);
            b.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256*1024);
            
            ChannelFuture f = b.bind(strHost, port);
            f.addListener(new MyChannelFutureListener(strHost, port));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public static void httpProxy2(String strHost, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 写120秒超时
                            ch.pipeline().addFirst("idleStateHandler", new IdleStateHandler(0, 120, 0));
                            // 写缓冲，只支持ByteBuf
                            ch.pipeline().addLast("writeBuffer", new WriteBufferHandler());
                            // 统计流入，流出字存数，只统计ByteBuf，其它不统计
                            ch.pipeline().addLast("upStreamDowmStreamStatistics", new UpStreamDowmStreamStatisticsHandler());
//                            ch.pipeline().addLast(new HttpProxyHandlerOutStatistics());
                            ch.pipeline().addLast(new org.vps.service.proxy.httppublic.HttpProxyHandler());
                            
                        }
                    }).option(ChannelOption.SO_BACKLOG, 256).childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 64*1024);
            b.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256*1024);
            
            ChannelFuture f = b.bind( port);
            f.addListener(new MyChannelFutureListener(strHost, port));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public static class MyChannelFutureListener implements ChannelFutureListener {
        private String host;
        private int port;

        public MyChannelFutureListener(String strHost, int port) {
            this.host = strHost;
            this.port = port;
        }

        public void operationComplete(ChannelFuture arg0) throws Exception {
            if (arg0.isSuccess()) {
            	if (logger.isInfoEnabled()) {
            		logger.info("服务启动成功！ host: " + host + ":" + port);
            	}
            }
        }
    }

    public static void main(String[] args) {
		ServiceEntry.httpProxy("0.0.0.0", 10401);
    }
    
}
