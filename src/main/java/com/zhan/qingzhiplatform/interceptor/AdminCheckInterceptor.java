package com.zhan.qingzhiplatform.interceptor;

import com.zhan.qingzhiplatform.pojo.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;


@Component
public class AdminCheckInterceptor implements HandlerInterceptor {

    private static final ObjectMapper mapper = new ObjectMapper();


    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        Integer role = (Integer) req.getAttribute("role");
        if (role == null || role != 2) {
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(mapper.writeValueAsString(Result.error("无管理员权限")));
            return false;
        }
        return true;
    }
}
