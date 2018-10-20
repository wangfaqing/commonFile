package org.vps.service.proxy.handler.common;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;

public interface IWriteBuffer {

    void write(ByteBuf buf, ChannelPromise promise);

    void write();

    void release();

}