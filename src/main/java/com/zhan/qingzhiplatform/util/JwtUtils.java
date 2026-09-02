package com.zhan.qingzhiplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration-ms:43200000}") long expirationMs) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT密钥不能为空");
        }
        if (expirationMs <= 0) {
            throw new IllegalArgumentException("JWT有效期必须大于0");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * 生成JWT令牌
     *
     * @param claims 载荷
     * @return JWT令牌
     */
    public String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .signWith(key, Jwts.SIG.HS256)
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .compact();
    }

    /**
     * 验签并解析JWT令牌
     *
     * @param jwt JWT令牌
     * @return 已验证的载荷
     */
    public Claims parseJwt(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public Integer getRole(Claims claims) {
        Object role = claims.get("role");
        return role instanceof Number ? ((Number) role).intValue() : null;
    }

    public Long getUserId(Claims claims) {
        Object userId = claims.get("userId");
        return userId instanceof Number ? ((Number) userId).longValue() : null;
    }
}
