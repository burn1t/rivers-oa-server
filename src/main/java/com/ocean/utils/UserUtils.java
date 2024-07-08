package com.ocean.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserUtils {

    /**
     * 获取登录用户名
     * @return String
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) return null;
        return authentication.getName();
    }

}
