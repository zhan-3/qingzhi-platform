package com.zhan.qingzhiplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志实体类
 * 记录登录尝试，用于防暴力破解
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginLogEntity {
    /**
     * 日志ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 登录IP
     */
    private String ipAddress;
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    /**
     * 登录结果：0失败 1成功
     */
    private Integer success;
}
