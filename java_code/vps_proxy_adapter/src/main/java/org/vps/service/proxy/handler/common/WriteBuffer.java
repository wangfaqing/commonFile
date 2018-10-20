package org.vps.service.proxy.handler.common;


import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class WriteBuffer implements IWriteBuffer {
    public static final Logger LOGGER = LoggerFactory.getLogger(WriteBuffer.class);
    
    private final static int BUFFER_SIZE_OF_WARN_LOG = 1024 * 1024 * 10;
    private final static int MAX_SIZE_OF_SIGNLE_BUF = 256 * 1024;
    private ChannelHandlerContext ctx;
    private boolean isRelease;
    private int totalBufSize = 0;
    private int maxBufSize = 0;
    private int totalSize = 0;
    private Queue<ByteBuf> queue;
    private ByteBuf lastEle;
    private ChannelPromise lastPromise;
    
    public WriteBuffer(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        queue = new LinkedList<>();
        isRelease = false;
    }
    
    /* (non-Javadoc)
     * @see org.vps.service.proxy.IWriteBuffer#write(io.netty.buffer.ByteBuf)
     */
    @Override
    public void write(ByteBuf buf, ChannelPromise promise) {
        synchronized (this) {
            if (isRelease) {
                // 代理连接已关闭，清掉buf, 退出
                buf.release();
                return ;
            }
            
            totalSize += buf.readableBytes();
            
            if (ctx.channel().isWritable() && queue.isEmpty()) {
                ctx.write(buf, promise);
                return;
            }
            
            if (lastPromise != null) {
                lastPromise.setSuccess();
                lastPromise = null;
            }

            lastPromise = promise;
            
            // 放入队列中，或追加到队列最后一个元素中
            totalBufSize += buf.readableBytes();
            if (queue.isEmpty()) {
                queue.add(buf);
                lastEle = buf;
            } else {
                if (lastEle.readableBytes() + buf.readableBytes() > MAX_SIZE_OF_SIGNLE_BUF) {
                    //超过设置的单包长度，
                    queue.add(buf);
                    lastEle = buf;
                } else {
                    lastEle.writeBytes(buf);
                    buf.release();
                }
            }
            
            if (totalBufSize > BUFFER_SIZE_OF_WARN_LOG) {
                if (totalBufSize > maxBufSize) {
                    maxBufSize = totalBufSize;
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.vps.service.proxy.IWriteBuffer#write()
     */
    @Override
    public void write() {
        synchronized (this) {
            if (queue.isEmpty()) {
                return ;
            }
            do {
                ByteBuf _buf = queue.poll();
                totalBufSize -= _buf.readableBytes();
                if (queue.isEmpty()) {
                    ctx.writeAndFlush(_buf, lastPromise);
                    lastPromise = null;
                    lastEle = null;
                    break;
                } else {
                    ctx.writeAndFlush(_buf, ctx.voidPromise());
                }
            } while (ctx.channel().isWritable());
        }
    } 
    
    /* (non-Javadoc)
     * @see org.vps.service.proxy.IWriteBuffer#release()
     */
    @Override
    public void release() {
        synchronized (this) {
            while (!queue.isEmpty()) {
                queue.poll().release();
            }
            lastEle = null;
            if (lastPromise != null) {
                lastPromise.cancel(false);
            }
            
            int size = totalBufSize;
            
            isRelease = true;
            totalBufSize = 0;
            
            if (maxBufSize >= BUFFER_SIZE_OF_WARN_LOG) {
                try {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("socks5代理缓冲区数据超过10M， now/max/total : {}/{}/{}", new Object[]{size, maxBufSize, totalSize});
//                        LogProxySession logProxySession = socks5ProxyAgent.getLogProxySession();
//                        if (logProxySession != null) {
//                            LOGGER.warn("socks5代理缓冲区数据超过10M， now/max/total : {}/{}/{}, url: {}, ip: {}, ct: {}, ipc: {}, auth: {}", 
//                                    new Object[]{size, maxBufSize, logProxySession.getSendByteCounts(), logProxySession.getUrl(), logProxySession.getIpRequest(), 
//                                            (int)Math.floor((logProxySession.getDisconnectTimestamp()-logProxySession.getConnectTimestamp())/1000), logProxySession.getIpConnect(), logProxySession.getAUZInfo()});
//                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    } 

}
