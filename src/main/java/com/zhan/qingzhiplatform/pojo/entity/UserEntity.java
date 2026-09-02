package com.zhan.qingzhiplatform.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 统一存储学生、教师、管理员三种角色
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 学号/工号
     */
    private String username;
    /**
     * 密码（bcrypt加密）
     */
    @JsonIgnore
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 院系
     */
    private String department;
    /**
     * 专业（仅学生）
     */
    private String major;
    /**
     * 角色：0学生 1教师 2管理员
     */
    private Integer role;
    /**
     * 状态：0禁用 1正常
     */
    private Integer status;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    /**
     * 软删除时间；为空表示有效
     */
    @JsonIgnore
    private LocalDateTime deletedAt;
}
