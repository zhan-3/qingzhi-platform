package com.zhan.qingzhiplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEntity {
    /**
     * 收藏ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 资源ID
     */
    private Long resourceId;
    /**
     * 收藏时间
     */
    private LocalDateTime createdAt;
}
