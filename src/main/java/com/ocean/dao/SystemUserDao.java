package com.ocean.dao;

import com.ocean.entity.SystemUser;
import org.apache.ibatis.annotations.Param;

public interface SystemUserDao {

    SystemUser selectUserByUsername(String username);

    String selectAuthorityByUsername(String username);

    void setAuthorityByUsername(@Param("username") String username,
                                @Param("authority") String authority);

    void deleteAuthorityByUsername(String username);

}
