package org.vps.service.proxy.socks5;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.service.proxy.AuthorizationCheckForSocks5;
import org.vps.service.proxy.AuthorizationInfo;
import org.vps.service.proxy.AuthorizationParse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
    
  
/** 
 * Handler implementation for the echo server. 
 */  

public class Socks5ProxyHandler extends ChannelInboundHandlerAdapter implements ISocks5ProxyHandler{  

    private static final Logger LOGGER = LoggerFactory.getLogger(Socks5ProxyHandler.class);
    
    private static final String HANDLE_NAME_INITIAL_REQUEST_DECODER         = "socks5InitialRequestDecoder";
    private static final String HANDLE_NAME_PASSWORDAUTH_REQUEST_DECODER    = "socks5PasswordAuthRequestDecoder";
    private static final String HANDLE_NAME_COMMAND_REQUEST_DECODER         = "socks5CommandRequestDecoder";
    
	private ChannelHandlerContext ctx;
	private DefaultSocks5CommandRequest cmdRequest;
	private String beferHandleName = HANDLE_NAME_INITIAL_REQUEST_DECODER;
	private Socks5ProxyDirectAdapter socks5ProxyDirectAdapter;
    private AuthorizationInfo authorizationInfo;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	    this.ctx = ctx;
        socks5ProxyDirectAdapter = new Socks5ProxyDirectAdapter(this, null);
        
	};
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (null != socks5ProxyDirectAdapter)
			socks5ProxyDirectAdapter.proxyChannelClose();
	};
	
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof DefaultSocks5InitialRequest) {
        	socks5ProxyDirectAdapter.setDefaultSocks5InitialRequest((DefaultSocks5InitialRequest)msg);
            Socks5InitialRequest((DefaultSocks5InitialRequest) msg);
        } else if (msg instanceof DefaultSocks5PasswordAuthRequest) {
        	socks5ProxyDirectAdapter.setDefaultSocks5PasswordAuthRequest((DefaultSocks5PasswordAuthRequest) msg);
            Socks5PasswordAuthRequest((DefaultSocks5PasswordAuthRequest) msg);
        } else if (msg instanceof DefaultSocks5CommandRequest) {
        	removeBeferHandler();
        	socks5ProxyDirectAdapter.transferData(msg);
        } else {
            ByteBuf byteBuf = (ByteBuf) msg;
            if (!socks5ProxyDirectAdapter.transferData(msg)) {
            	//未尝试发送，自己 release Bytebuf
                byteBuf.release();
            }
        }
    }  
  
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("水位变化");
        if (ctx.channel().isWritable()) {
//            LOGGER.info("可以写数据");
        	if (null != socks5ProxyDirectAdapter) {
                socks5ProxyDirectAdapter.readAsync();
        	}
            return ;
        }
//        LOGGER.info("无法写数据");
    }
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	ctx.close();
    } 
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                if (LOGGER.isInfoEnabled()) LOGGER.info("socks5 write idle.");
                ctx.close();
            } else if (event.state() == IdleState.ALL_IDLE) {
                if (LOGGER.isWarnEnabled()) LOGGER.warn("socks5 all idle.");
                ctx.close();
            }
        }
    }

    @Override
    public String socks5GetRemoteAddr() {
        return ctx.channel().remoteAddress().toString();
    }
    
    @Override
    public void socks5ChannelClose() {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    
    @Override
    public void socks5ResponseCmdConnectSuc() {
        DefaultSocks5CommandResponse response 
            = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
        ctx.writeAndFlush(response);
    }

    @Override
    public void socks5ResponseCmdConnectErr() {
        DefaultSocks5CommandResponse response = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, cmdRequest.dstAddrType());
        ctx.writeAndFlush(response);
    }

    @Override
    public void socks5ResponseData(ByteBuf data) {
        ctx.writeAndFlush(data);
    }

    private void Socks5InitialRequest(DefaultSocks5InitialRequest defaultSocks5InitialRequest) {
        DefaultSocks5InitialResponse response = new DefaultSocks5InitialResponse(Socks5AuthMethod.UNACCEPTED);
        List<Socks5AuthMethod> methods = defaultSocks5InitialRequest.authMethods();
        for(Socks5AuthMethod method : methods) {
            /*if (method == Socks5AuthMethod.NO_AUTH) {
                ctx.pipeline().remove("socks5InitialRequestDecoder");
                ctx.pipeline().addBefore(ctx.name(), "socks5CommandRequestDecoder", new Socks5CommandRequestDecoder());
                response = new DefaultSocks5InitialResponse(method);
                break;
            } else */if (method == Socks5AuthMethod.PASSWORD) {
                removeBeferHandlerAndAddNew(HANDLE_NAME_PASSWORDAUTH_REQUEST_DECODER, new Socks5PasswordAuthRequestDecoder());
                response = new DefaultSocks5InitialResponse(method);
                break;
            }/* else {
                response = new DefaultSocks5InitialResponse(Socks5AuthMethod.UNACCEPTED);
            }*/
        }
        if (response != null) {
            ctx.writeAndFlush(response);
        }
    }
    
    private void Socks5PasswordAuthRequest(DefaultSocks5PasswordAuthRequest defaultSocks5PasswordAuthRequest) {
        DefaultSocks5PasswordAuthResponse response = null;
        
        removeBeferHandlerAndAddNew(HANDLE_NAME_COMMAND_REQUEST_DECODER, new Socks5CommandRequestDecoder());
                
        String name = defaultSocks5PasswordAuthRequest.username();
        String pwd = jodd.util.URLDecoder.decode(defaultSocks5PasswordAuthRequest.password());
        
        if (isPass(name, pwd)){
            if ( !socks5ProxyDirectAdapter.connectSelect(name,pwd) ) {
            	if (LOGGER.isWarnEnabled()) {
            		LOGGER.warn("连接获取的代理服务器失败");
            	}
            	
            	response = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
                ctx.writeAndFlush(response);
                socks5ChannelClose();
                return;
            }
        } else {
            response = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
            ctx.writeAndFlush(response);
            socks5ChannelClose();
        }
    }
    
    private void removeBeferHandlerAndAddNew(String newName, ChannelHandler handler) {
        ctx.pipeline().remove(beferHandleName);
        ctx.pipeline().addFirst(newName, handler);
        beferHandleName = newName;
    }
    
    private void removeBeferHandler() {
        ctx.pipeline().remove(beferHandleName);
        beferHandleName = "";
    }

    @Override
    public boolean socks5ResponseWritable() {
        return ctx.channel().isWritable();
    }

	@Override
	public void socks5ResponsePasswordAuthSuc() {
		DefaultSocks5PasswordAuthResponse response = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS);
		ctx.channel().writeAndFlush(response);
	}

	@Override
	public void socks5ResponsePasswordAuthErr() {
		DefaultSocks5PasswordAuthResponse response = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
		ctx.channel().writeAndFlush(response);
	}
	
	public boolean isPass(String account, String pwd) {
	    authorizationInfo = AuthorizationParse.parse(account, pwd);
        return AuthorizationCheckForSocks5.isPass(null, authorizationInfo);
    }

} 