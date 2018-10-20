package org.vps.service.proxy.handler.common;


public interface IUpStreamDowmStreamStatistics {
    public void upStreamInc(int size);
    public void downStreamInc(int size);
}
