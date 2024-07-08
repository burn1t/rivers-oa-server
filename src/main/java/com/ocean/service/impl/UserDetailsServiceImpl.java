package com.ocean.service.impl;

import com.ocean.dao.SystemUserDao;
import com.ocean.entity.SecuritySystemUser;
import com.ocean.entity.SystemUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("userDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final SystemUserDao systemUserDao;

    public UserDetailsServiceImpl(SystemUserDao systemUserDao) {
        this.systemUserDao = systemUserDao;
    }

    /**
     * 根据用户名进行认证
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取用户信息
        SystemUser user = systemUserDao.selectUserByUsername(username);
//        System.out.println(user);
        if (Objects.isNull(user)) throw new UsernameNotFoundException("当前用户不存在");
        // 获取用户权限
        List<String> permissionValueList = new ArrayList<>();

        SecuritySystemUser securityUser = new SecuritySystemUser();
        securityUser.setCurrentUserInfo(user);
        securityUser.setPermissionValueList(permissionValueList);
        return securityUser;
    }
}
