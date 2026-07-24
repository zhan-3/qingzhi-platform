package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.dto.LoginDTO;
import com.zhan.qingzhiplatform.pojo.dto.RegisterDTO;
import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "认证接口")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     *
     * @param dto 注册DTO
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "个人用学号/工号注册")
    @SecurityRequirement(name = "")  // 移除swagger全局锁
    public Result register(@Valid @RequestBody RegisterDTO dto) {
        log.info("用户注册: {}", dto);
        authService.register(dto);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     *
     * @param dto 登录DTO
     * @return 登录结果 和 jwt
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码进行认证")
    @SecurityRequirement(name = "")  // 移除swagger全局锁
    public Result login(@RequestBody LoginDTO dto){
        log.info("用户登录: {}", dto);
        String token = authService.login(dto);
        return Result.success(token);
    }

}
