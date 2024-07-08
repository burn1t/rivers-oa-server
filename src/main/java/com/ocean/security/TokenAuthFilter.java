package com.ocean.security;

import com.ocean.dao.SystemUserDao;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenAuthFilter extends BasicAuthenticationFilter {
    private final TokenManager tokenManager;
    private final SystemUserDao systemUserDao;


    public TokenAuthFilter(AuthenticationManager authenticationManager,TokenManager tokenManager, SystemUserDao systemUserDao) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
        this.systemUserDao = systemUserDao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = request.getHeader("token");
//        System.out.println("TokenAuthFilter-token: " + token);

        // 构建 Group 组信息
        List<SimpleGrantedAuthority> groupList = new ArrayList<>();
        // 注意 , 该权限是必须的
        groupList.add(new SimpleGrantedAuthority("ROLE_ACTIVITI_USER"));
        groupList.add(new SimpleGrantedAuthority("ADMIN"));

        try {
            // 解析 Token
            if (!StringUtils.isEmpty(token)) {
                String username = tokenManager.getUsernameFormToken(token);
//                System.out.println("TokenAuthFilter-username: " + username);
                // 获取对应权限
//            String permissionValue = systemUserDao.getAuthorityByUsername(username);
//            List<String> permissionValueList = Arrays.asList(permissionValue.split(","));
//            // 存入权限上下文
//            Collection<GrantedAuthority> authorities = new ArrayList<>();
//            if (!CollectionUtils.isEmpty(permissionValueList)) {
//                for (String permission : permissionValueList) {
//                    SimpleGrantedAuthority authority =
//                    new SimpleGrantedAuthority(permission);
//                    authorities.add(authority);
//                }
//            }
                // 生成权限信息对象
                /*
                * UsernamePasswordAuthenticationToken 对象
                * 通常会传递给 AuthenticationManager 进行身份验证
                * 如果身份验证成功，则会创建一个 Authentication 对象来表示已验证的身份
                * */
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(username, token, groupList);
                // 存入权限上下文
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (MalformedJwtException | SignatureException | ExpiredJwtException | UnsupportedJwtException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 放行
            chain.doFilter(request, response);
        }
    }
}
