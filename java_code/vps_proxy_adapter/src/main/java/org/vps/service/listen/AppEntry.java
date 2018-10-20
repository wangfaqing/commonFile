package org.vps.service.listen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.service.business.BusinessServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class AppEntry {
	public static final Logger logger = LoggerFactory.getLogger(AppEntry.class);

	/**
	 * @author wangyaofeng
	 * @param port
	 * @des 启动业务服务
	 */
	public static void business(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							
							// 60s keep alive timeout
                            pipeline.addFirst("idleStateHandler", new IdleStateHandler(0, 0, 60));
							
							// http-request解码器 http服务器端对request解码
							pipeline.addLast("decoder",new HttpRequestDecoder());
							//http-response解码器 http服务器端对response编码
							pipeline.addLast("encoder",new HttpResponseEncoder());
							pipeline.addLast("aggregator",new HttpObjectAggregator(1048576));
							pipeline.addLast("deflater",new HttpContentCompressor());
							pipeline.addLast("handler",new BusinessServerHandler());
						}
					});
			  b.bind(port);
		} catch(Exception e){
			logger.debug(e.getMessage(),e);
		}
		
	}

}
