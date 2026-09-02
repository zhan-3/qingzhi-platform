package com.zhan.qingzhiplatform.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    private static final String TEST_SECRET = "01234567890123456789012345678901";

    @Test
    void generatedTokenCanBeParsedOnceAndClaimsReused() {
        JwtUtils jwtUtils = new JwtUtils(TEST_SECRET, 60_000L);
        String token = jwtUtils.generateJwt(Map.of(
                "username", "student01",
                "userId", 12L,
                "role", 0
        ));

        Claims claims = jwtUtils.parseJwt(token);

        assertEquals(12L, jwtUtils.getUserId(claims));
        assertEquals(0, jwtUtils.getRole(claims));
        assertEquals("student01", claims.get("username"));
    }

    @Test
    void rejectsEmptySecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtils(" ", 60_000L));
    }

    @Test
    void rejectsNonPositiveExpiration() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtils(TEST_SECRET, 0));
    }
}
