package org.vps.service.workdirect;

import io.netty.buffer.ByteBuf;

public interface ISorks5ProxyDirect {
    public void recv(ByteBuf data);
    public void notifyClose();
    public void readComplete();
    public void sendPer(int step , Object msg);
}
