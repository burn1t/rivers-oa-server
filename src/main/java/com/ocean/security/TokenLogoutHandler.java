package com.ocean.security;


import com.ocean.dao.SystemUserDao;
import com.ocean.utils.ResponseUtil;
import com.ocean.utils.ResultModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenLogoutHandler implements LogoutHandler {

    private TokenManager tokenManager;
    private SystemUserDao systemUserDao;

    public TokenLogoutHandler(TokenManager tokenManager, SystemUserDao systemUserDao) {
        this.tokenManager = tokenManager;
        this.systemUserDao = systemUserDao;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getHeader("token");
//        System.out.println("logout: " + token);
        if (!StringUtils.isEmpty(token)) {
            // 解析 Token
            String username = null;
            try {
                username = tokenManager.getUsernameFormToken(token);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("logout: " + username);
            // 删除数据库数据
            systemUserDao.deleteAuthorityByUsername(username);
        }
    }



}
