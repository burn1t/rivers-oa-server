package com.ocean.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocean.dao.SystemUserDao;
import com.ocean.entity.SecuritySystemUser;
import com.ocean.entity.SystemUser;
import com.ocean.utils.ResponseUtil;
import com.ocean.utils.ResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TokenLoginFiler extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final SystemUserDao systemUserDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenLoginFiler(AuthenticationManager authenticationManager, TokenManager tokenManager, SystemUserDao systemUserDao) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.systemUserDao = systemUserDao;
        this.setPostOnly(false);
        this.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher("/user/login", "POST")
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 获取表单提供的数据
        try {
            SystemUser user = objectMapper.readValue(request.getInputStream(), SystemUser.class);
            // Security 负责校验
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword()));
//            log.info(String.valueOf(authenticate));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 获取用户名
        SecuritySystemUser user = (SecuritySystemUser) authResult.getPrincipal();

        String username = user.getUsername();
        List<String> authority = user.getPermissionValueList();
        String token = tokenManager.createToken(username);
        // 存入对应权限
        systemUserDao.setAuthorityByUsername(username, String.join(",", authority));
        // 返回 Token
        response.setCharacterEncoding("utf-8");
        ResponseUtil.out(response, ResultModel.success("登录成功", token));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        ResponseUtil.out(response, ResultModel.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误"));
    }
}
