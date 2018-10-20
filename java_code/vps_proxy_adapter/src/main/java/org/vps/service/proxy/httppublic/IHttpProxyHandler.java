package org.vps.service.proxy.httppublic;


import io.netty.buffer.ByteBuf;

public interface IHttpProxyHandler {
    
    public String httpGetRemoteAddr();
    
    public void httpChannelClose();
    
    public void httpResponseError(String response);
    
    public void httpResponse(String response);
    
    public void httpResponse(ByteBuf data);

}
