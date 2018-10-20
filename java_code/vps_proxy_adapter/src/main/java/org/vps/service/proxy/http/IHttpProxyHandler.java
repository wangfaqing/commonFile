package org.vps.service.proxy.http;


import io.netty.buffer.ByteBuf;

public interface IHttpProxyHandler {
    
    public String httpGetRemoteAddr();
    
    public void httpChannelClose();
    
    public void httpResponseError(String response);
    
    public void httpResponse(String response);
    
    public void httpResponse(ByteBuf data);

}
