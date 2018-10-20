package org.vps.app.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vps.app.log.dao.modle.PublicUser;

public class PublicUsers {
    public static final Logger LOGGER = LoggerFactory.getLogger(PublicUsers.class);
    private AtomicReference<Map<String, PublicUser>> atomicReferenceMap;
    
    public PublicUsers(List<PublicUser> publicUsers) {
        atomicReferenceMap = new AtomicReference<Map<String, PublicUser>>(null);
        setPublicUsers(publicUsers);    
    }
    
    public void setPublicUsers(List<PublicUser> publicUsers) {
        Map<String, PublicUser> map = new HashMap<>();
        for(PublicUser publicUser : publicUsers) {
            if (publicUser.getUsed()) {
                map.put(publicUser.getUser(), publicUser);
            }
        }
        atomicReferenceMap.set(map);
    }
    
    public PublicUser getPublicUser(String publciUserName) {
        Map<String, PublicUser> map = atomicReferenceMap.get();
        
        if (map == null) return null;
        
        return map.get(publciUserName);
    }
    
}
