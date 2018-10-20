package org.vps.app.log.dao.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoMapper {
    String getSecretById(long userId);
}
