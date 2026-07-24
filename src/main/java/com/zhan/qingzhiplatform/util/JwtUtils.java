package com.zhan.qingzhiplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JwtUtils {
    // 加密密钥
    private static final String secret = "replace-with-at-least-32-characters";
    private static final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));


    /**
     * 生成Jwt token
     *
     * @param claims 载荷
     * @return token
     */
    public static String generateJwt(Map<String, Object> claims) {
        Long expire = 43200000L;
        return Jwts.builder()
                .claims(claims)
                .signWith(key, Jwts.SIG.HS256)
                .expiration(new java.util.Date(System.currentTimeMillis() + expire)) // 12 hours expiration
                .compact();
    }

    /**
     * 解析Jwt token
     *
     * @param jwt token
     * @return 解析载荷
     */
    public static Claims ParseJwt(String jwt){
        return Jwts.parser().
                verifyWith(key).
                build().
                parseSignedClaims(jwt).
                getPayload();
    }

    /**
     * 获取token载荷角色
     *
     * @param token token
     * @return 对应角色
     */
    public static Integer getRole(String token){
        return (Integer) ParseJwt(token).get("role");
    }

    /**
     * 获取token载荷用户ID
     * @param token token
     * @return 用户ID
     */
    public static Long getUserId(String token){
        Object id = ParseJwt(token).get("userId");
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }
}
