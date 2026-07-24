package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.pojo.dto.ChangePasswordDTO;
import com.zhan.qingzhiplatform.pojo.dto.ProfileUpdateDTO;
import com.zhan.qingzhiplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "个人信息", description = "个人信息管理接口")
@RequestMapping("/user")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public Result getUserProfile(@RequestAttribute Long userId) {
        return Result.success(userService.getUserById(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "修改个人信息")
    public Result updateUserProfile(@RequestBody ProfileUpdateDTO dto, @RequestAttribute Long userId) {
        userService.updateUserProfile(userId,
                dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getDepartment(), dto.getMajor());
        return Result.success("修改成功");
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result changeUserPassword(@RequestBody ChangePasswordDTO dto, @RequestAttribute Long userId) {
        userService.changeUserPassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success("密码修改成功");
    }
}
