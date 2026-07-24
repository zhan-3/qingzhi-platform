package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.dto.ResetPasswordDTO;
import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import com.zhan.qingzhiplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "管理员用户管理", description = "用户管理相关的接口")
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 管理员分页查询用户
     *
     * @param page 当前页码
     * @param pageSize 每页条数
     * @return 查询结果
     */
    @GetMapping
    @Operation(summary = "分页查询用户")
    public Result listUsers(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.listUsers(page, pageSize));
    }

    /**
     * 管理员查询用户信息
     *
     * @param id 用户ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详细信息")
    public Result getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 管理员更新用户信息
     *
     * @param id 用户ID
     * @param user 新用户实体
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public Result updateUser(@PathVariable Long id, @RequestBody UserEntity user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success("修改成功");
    }

    /**
     * 管理员删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    /**
     * 管理员重置用户密码
     *
     * @param id 用户ID
     * @param body 密码参数
     * @return 重置结果
     */
    @PutMapping("/{id}/reset-password")
    @Operation(summary = "重置用户密码")
    public Result resetUserPassword(@PathVariable Long id, @RequestBody ResetPasswordDTO dto) {
        userService.resetUserPassword(id, dto.getPassword());
        return Result.success("密码重置成功");
    }

    /**
     * 管理员批量导入注册用户
     *
     * @param file 用户excel文件
     * @return 导入注册结果
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量导入注册用户", description = "上传 Excel 文件（.xlsx 或 .xls），批量注册用户")
    public Result batchImportUsers(
            @RequestParam("file")
            @Parameter(description = "Excel 文件（.xlsx 或 .xls）", required = true)
            MultipartFile file) {
        log.info("批量导入用户: fileName={}", file.getOriginalFilename());
        return Result.success(userService.batchImportUsers(file));
    }
}
