package com.zhan.qingzhiplatform.interceptor;

import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    
    private static final ObjectMapper mapper = new ObjectMapper();



    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeError(resp, "NOT_LOGIN");
        }

        String token = authHeader.substring(7);

        try {
            JwtUtils.ParseJwt(token);
        } catch (Exception e) {
            log.error("token解析失败");
            return writeError(resp, "NOT_LOGIN");
        }

        req.setAttribute("userId", JwtUtils.getUserId(token));
        req.setAttribute("role", JwtUtils.getRole(token));
        return true;
    }

    private boolean writeError(HttpServletResponse resp, String msg) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(mapper.writeValueAsString(Result.error(msg)));
        return false;
    }
}
