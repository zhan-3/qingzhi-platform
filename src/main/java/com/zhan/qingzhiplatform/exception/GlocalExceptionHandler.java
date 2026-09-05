package com.zhan.qingzhiplatform.exception;

import com.zhan.qingzhiplatform.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice  // 全局切面,作用于所有Controller
public class GlocalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(msg);
    }

    @ExceptionHandler(BusinessException.class)
    public Result handleBusiness(BusinessException ex) {
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        log.error("系统异常: ", ex);
        return Result.error("系统异常, 请稍后重试");
    }
}
