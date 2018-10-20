package org.vps.service.proxy;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vps.app.log.dao.modle.PublicUser;

@Service("publicUserOrderMgr")
public class PublicUserOrderMgr {
    public static final Logger LOGGER = LoggerFactory.getLogger(PublicUserOrderMgr.class);
    
    private Map<String, PublicUserOrder> mapPublicUserOrder;
    
    public PublicUserOrderMgr() {
        mapPublicUserOrder = new HashMap<>();
//        updatePublicUsers(publicUsers);
    }
    
//    public void updatePublicUsers(List<PublicUser> publicUsers) {
//        
//        for(PublicUser publicUser : publicUsers) {
//            PublicUserOrder publicUserOrder = mapPublicUserOrder.get(publicUser.getUser());
//            if (publicUser.getUsed()) {
//                if (publicUserOrder != null) {
//                    // 如果被标记删除，修改标记
//                } else {
//                    publicUserOrder = new PublicUserOrder();
//                    publicUserOrder.setUser(publicUser);
//                    mapPublicUserOrder.put(publicUser.getUser(), publicUserOrder);    
//                }
//            } else {
//                if (publicUserOrder != null) {
//                    // 如果被标记删除，修改标记
//                } else {
//                    // TODO 标记可删除   
//                }
//            }
//        }
//    }
    
//    public boolean updatePublicUserReq(PublicUser publicUser) {
//        PublicUserOrder publicUserOrder = mapPublicUserOrder.get(publicUser.getUser());
//        if (publicUserOrder == null) {
//            publicUserOrder = new PublicUserOrder();
//            publicUserOrder.setUser(publicUser);
//        }
//        
//        return publicUserOrder.inrReq();
//    }
    
    public void updatePublicUserFlow(PublicUser publicUser, long flow) {
        PublicUserOrder publicUserOrder = mapPublicUserOrder.get(publicUser.getUser());
        if (publicUserOrder == null) {
            publicUserOrder = new PublicUserOrder();
            publicUserOrder.setUser(publicUser);
        }
        
        publicUserOrder.update(flow);
    }
    
    public boolean checkPublicUser(PublicUser publicUser) {
        PublicUserOrder publicUserOrder = mapPublicUserOrder.get(publicUser.getUser());
        if (publicUserOrder == null) {
            publicUserOrder = new PublicUserOrder();
            publicUserOrder.setUser(publicUser);
        }
        
        return publicUserOrder.check();
    }
    
    public boolean ConnPublicUser(PublicUser publicUser) {
        PublicUserOrder publicUserOrder = mapPublicUserOrder.get(publicUser.getUser());
        if (publicUserOrder == null) {
            publicUserOrder = new PublicUserOrder();
            publicUserOrder.setUser(publicUser);
            mapPublicUserOrder.put(publicUser.getUser(), publicUserOrder);
        }
        
        if (publicUserOrder.getConn() >= publicUser.getConLimit()) {
            return false;
        } else {
            publicUserOrder.inrConn();
            return true;
        }
    }
    
    public void DisconnPublicUser(PublicUser publicUser) {
        PublicUserOrder publicUserOrder = mapPublicUserOrder.get(publicUser.getUser());
        if (publicUserOrder == null) {
            return ;
        }
        
        publicUserOrder.decrConn();
    }
    
}
