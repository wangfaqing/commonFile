package org.vps.service.workdirect;

import io.netty.buffer.ByteBuf;

public interface IHttpProxyDirect {
    public void recv(ByteBuf data);
    public void notifyClose();
    public void readComplete();
    
}
