package org.vps.service.proxy.socks5;


import io.netty.buffer.ByteBuf;

public interface ISocks5ProxyHandler {
    
    public String socks5GetRemoteAddr();
    
    public void socks5ChannelClose();
    
    public void socks5ResponseCmdConnectSuc();
    
    public void socks5ResponseCmdConnectErr();
    
    public void socks5ResponseData(ByteBuf data);
    
    public boolean socks5ResponseWritable();

    public void socks5ResponsePasswordAuthSuc();
    
    public void socks5ResponsePasswordAuthErr();
}
