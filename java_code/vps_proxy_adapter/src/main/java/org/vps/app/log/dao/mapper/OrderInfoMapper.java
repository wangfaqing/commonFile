package org.vps.app.log.dao.mapper;

import org.springframework.stereotype.Repository;
import org.vps.app.log.dao.modle.OrderInfo;

@Repository
public interface OrderInfoMapper {
    OrderInfo getOrderById(String orderId);
}
