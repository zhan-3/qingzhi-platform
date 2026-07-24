package com.zhan.qingzhiplatform.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "学号/工号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String name;
    private String phone;
    private String email;
    private String department;
    private String major;

    @NotNull(message = "角色不能为空")
    private Integer role;
}
