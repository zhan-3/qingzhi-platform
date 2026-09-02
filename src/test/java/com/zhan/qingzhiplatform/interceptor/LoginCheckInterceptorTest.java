package com.zhan.qingzhiplatform.interceptor;

import com.zhan.qingzhiplatform.mapper.UserMapper;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
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
        UserMapper userMapper = mock(UserMapper.class);
        Claims claims = mock(Claims.class);
        when(jwtUtils.parseJwt("token-value")).thenReturn(claims);
        when(jwtUtils.getUserId(claims)).thenReturn(7L);
        when(jwtUtils.getRole(claims)).thenReturn(2);
        when(userMapper.getById(7L)).thenReturn(new UserEntity());

        LoginCheckInterceptor interceptor = new LoginCheckInterceptor(jwtUtils, userMapper);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(7L, request.getAttribute("userId"));
        assertEquals(2, request.getAttribute("role"));
        verify(jwtUtils).parseJwt("token-value");
        verify(userMapper).getById(7L);
    }

    @Test
    void rejectsTokenForSoftDeletedUser() throws Exception {
        JwtUtils jwtUtils = mock(JwtUtils.class);
        UserMapper userMapper = mock(UserMapper.class);
        Claims claims = mock(Claims.class);
        when(jwtUtils.parseJwt("token-value")).thenReturn(claims);
        when(jwtUtils.getUserId(claims)).thenReturn(7L);
        when(jwtUtils.getRole(claims)).thenReturn(0);
        when(userMapper.getById(7L)).thenReturn(null);

        LoginCheckInterceptor interceptor = new LoginCheckInterceptor(jwtUtils, userMapper);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();

        org.junit.jupiter.api.Assertions.assertFalse(
                interceptor.preHandle(request, response, new Object())
        );
        assertTrue(response.getContentAsString().contains("NOT_LOGIN"));
    }
}
