package com.ocean.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenManager {
    // Token 私钥
//    @Value("{token.security-key}")
    private String securityKey = "HfkjksFKLJISJFKLFKWJFQFIQWIOFJQOFFQGGSDGFFJIQOEUFIEJFIOQWEFHFQOK5FKOIQWUFFEFE423FIQEOFJHUEWHFKASKDLQWJIFSJDJKFHJIJWO";
    // 过期时间：7 days
    private final long exp = 60*60*24*7*1000;

    /**
     * 生成 Token
     * @param username 用户名
     * @return {@link String}
     */
    public String createToken(String username) {
        String token = Jwts.builder().setSubject(username)
                // 设置过期时间
                .setExpiration(new Date(System.currentTimeMillis()+ exp))
                //秘钥加密部分
                .signWith(SignatureAlgorithm.HS256, securityKey).compact();
        return token;
    }

    /**
     * JWT 解析 Token 获取用户名
     * @param token 令牌
     * @return {@link String}
     */
    public String getUsernameFormToken(String token) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(securityKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


}
