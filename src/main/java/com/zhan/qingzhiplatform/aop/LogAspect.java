package com.zhan.qingzhiplatform.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 * 请求上下文日志和异常信息
 */

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.zhan.qingzhiplatform.controller..*(..))")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attrs != null ? attrs.getRequest() : null;
        if (req == null) return joinPoint.proceed();

        String method = req.getMethod();
        String uri = req.getRequestURI();
        Object userId = req.getAttribute("userId");
        String user = userId != null ? "uid:" + userId : "guest";

        // 简要记录 GET 请求
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            long start = System.currentTimeMillis();
            try {
                Object result = joinPoint.proceed();
                log.info("[{}] GET {} | {}ms", user, uri, System.currentTimeMillis() - start);
                return result;
            } catch (Exception e) {
                log.warn("[{}] GET {} | {}ms | {}", user, uri, System.currentTimeMillis() - start, e.getMessage());
                throw e;
            }
        }

        // 构造请求参数(脱敏 + 截断)
        StringBuilder params = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest || arg instanceof MultipartFile) continue;
            String s = String.valueOf(arg);
            // 密码脱敏
            s = s.replaceAll("password=([^,)]+)", "password=***");
            s = s.replaceAll("oldPassword=([^,)]+)", "oldPassword=***");
            s = s.replaceAll("newPassword=([^,)]+)", "newPassword=***");
            if (s.length() > 200) s = s.substring(0, 200) + "...";
            params.append(s).append(" ");
        }

        // 执行业务并输出日志
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("[{}] {} {} | {} | {}ms", user, method, uri, params.toString().trim(), System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.warn("[{}] {} {} | {} | {}ms | {}", user, method, uri, params.toString().trim(), System.currentTimeMillis() - start, e.getMessage());
            throw e;
        }
    }
}
