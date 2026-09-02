package com.zhan.qingzhiplatform.interceptor;

import com.zhan.qingzhiplatform.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginCheckInterceptorTest {

    @Test
    void parsesTokenOnceAndStoresVerifiedClaimsOnRequest() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        Claims claims = mock(Claims.class);
        when(jwtUtils.parseJwt("token-value")).thenReturn(claims);
        when(jwtUtils.getUserId(claims)).thenReturn(7L);
        when(jwtUtils.getRole(claims)).thenReturn(2);

        LoginCheckInterceptor interceptor = new LoginCheckInterceptor(jwtUtils);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(7L, request.getAttribute("userId"));
        assertEquals(2, request.getAttribute("role"));
        verify(jwtUtils).parseJwt("token-value");
    }
}
